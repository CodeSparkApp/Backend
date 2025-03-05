package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.MessageSenderType;
import de.dhbw.tinf22b6.codespark.api.exception.ChatStreamingException;
import de.dhbw.tinf22b6.codespark.api.exception.StreamWritingException;
import de.dhbw.tinf22b6.codespark.api.exception.UserNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import de.dhbw.tinf22b6.codespark.api.model.ConversationMessage;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.repository.ConversationRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConversationServiceImpl implements ConversationService {
	private final SimpleOpenAI simpleOpenAI;
	private final Environment env;
	private final AccountRepository accountRepository;
	private final ConversationRepository conversationRepository;

	public ConversationServiceImpl(@Autowired SimpleOpenAI simpleOpenAI,
								   @Autowired Environment env,
								   @Autowired AccountRepository accountRepository,
								   @Autowired ConversationRepository conversationRepository) {
		this.simpleOpenAI = simpleOpenAI;
		this.env = env;
		this.accountRepository = accountRepository;
		this.conversationRepository = conversationRepository;
	}

	@Override
	@Transactional
	public String processPrompt(UUID accountId, PromptRequest request) {
		Conversation conversation =  getOrCreateConversation(accountId);
		List<ChatMessage> chatHistory = parseConversation(conversation);
		chatHistory.add(ChatMessage.UserMessage.of(request.getPrompt()));

		// Build request with existing history
		ChatRequest chatRequest = ChatRequest.builder()
				.model(env.getRequiredProperty("openai.model.name"))
				.temperature(0.4)
				.messages(chatHistory)
				.build();

		CompletableFuture<Chat> futureChat = simpleOpenAI.chatCompletions().create(chatRequest);
		Chat chatResponse = futureChat.join();
		String response = chatResponse.firstContent();

		// Save prompt and response to history
		conversation.addMessage(new ConversationMessage(MessageSenderType.USER, request.getPrompt()));
		conversation.addMessage(new ConversationMessage(MessageSenderType.ASSISTANT, response));
		conversationRepository.save(conversation);

		return response;
	}

	@Override
	@Transactional
	public StreamingResponseBody processPromptStream(UUID accountId, PromptRequest request) {
		Conversation conversation =  getOrCreateConversation(accountId);
		List<ChatMessage> chatHistory = parseConversation(conversation);
		chatHistory.add(ChatMessage.UserMessage.of(request.getPrompt()));

		conversation.addMessage(new ConversationMessage(MessageSenderType.USER, request.getPrompt()));
		conversationRepository.save(conversation);

		// Build request with existing history
		ChatRequest chatRequest = ChatRequest.builder()
				.model(env.getRequiredProperty("openai.model.name"))
				.temperature(0.4)
				.messages(chatHistory)
				.stream(true)
				.build();

		return outputStream -> {
			try (outputStream; Stream<Chat> stream = simpleOpenAI.chatCompletions().createStream(chatRequest).join()) {
				// Save assistant response
				StringBuilder response = new StringBuilder();

				stream.forEach(chunk -> {
					String messageChunk = chunk.getChoices().stream()
							.findFirst()
							.map(choice -> choice.getMessage().getContent())
							.orElse("");

					if (!messageChunk.isEmpty()) {
						try {
							// Send chunk to client
							outputStream.write(messageChunk.getBytes(StandardCharsets.UTF_8));
							outputStream.flush();

							// Append chunk to assistant response
							response.append(messageChunk);
						} catch (IOException e) {
							throw new StreamWritingException("An error occurred while writing to the output stream");
						}
					}
				});

				// Save the complete assistant response to conversation
				if (!response.isEmpty()) {
					conversation.addMessage(new ConversationMessage(MessageSenderType.ASSISTANT, response.toString()));
					conversationRepository.save(conversation);
				}
			} catch (Exception e) {
				throw new ChatStreamingException("An error occurred while processing the streaming response");
			}
		};
	}

	private Conversation getOrCreateConversation(UUID userId) {
		Account account = accountRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("No account was found with the provided information"));

		return conversationRepository.findByAccount(account)
				.orElseGet(() -> new Conversation(account));
	}

	private List<ChatMessage> parseConversation(Conversation conversation) {
		return conversation.getMessages().stream()
				.map(m -> switch (m.getSenderType()) {
					case USER -> ChatMessage.UserMessage.of(m.getMessage());
					case ASSISTANT -> ChatMessage.AssistantMessage.of(m.getMessage());
					default -> ChatMessage.SystemMessage.of(m.getMessage());
				})
				.collect(Collectors.toList());
	}
}
