package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.MessageSenderType;
import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import de.dhbw.tinf22b6.codespark.api.model.ConversationMessage;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.repository.ConversationRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConversationServiceImpl implements ConversationService {
	private final SimpleOpenAI openAI;
	private final ChatRequest.ChatRequestBuilder chatRequestBase;
	private final ConversationRepository conversationRepository;

	public ConversationServiceImpl(@Autowired Environment env,
								   @Autowired ConversationRepository conversationRepository) {
		this.openAI = SimpleOpenAI.builder()
				.apiKey(env.getRequiredProperty("openai.api.key"))
				.organizationId(env.getRequiredProperty("openai.api.organization_id"))
				.projectId(env.getRequiredProperty("openai.api.project_id"))
				.build();

		this.chatRequestBase = ChatRequest.builder()
				.model(env.getRequiredProperty("openai.model.name"))
				.temperature(0.4);
				// .maxCompletionTokens(500);

		this.conversationRepository = conversationRepository;
	}

	@Override
	public String processPrompt(UUID userId, PromptRequest request) {
		// Retrieve previous conversation
		Conversation conversation = conversationRepository.findByUserId(userId)
				.orElseGet(() -> new Conversation(userId));

		List<ChatMessage> chatHistory = parseConversation(conversation);
		chatHistory.add(ChatMessage.UserMessage.of(request.getPrompt()));

		// Build request with existing history
		ChatRequest chatRequest = chatRequestBase
				.messages(chatHistory)
				.build();

		CompletableFuture<Chat> futureChat = openAI.chatCompletions().create(chatRequest);
		Chat chatResponse = futureChat.join();
		String response = chatResponse.firstContent();

		// Save prompt and response to history
		conversation.addMessage(new ConversationMessage(MessageSenderType.USER, request.getPrompt()));
		conversation.addMessage(new ConversationMessage(MessageSenderType.ASSISTANT, response));
		conversationRepository.save(conversation);

		return response;
	}

	@Override
	public StreamingResponseBody processPromptStream(UUID userId, PromptRequest request) {
		// Retrieve previous conversation
		Conversation conversation = conversationRepository.findByUserId(userId)
				.orElseGet(() -> new Conversation(userId));

		List<ChatMessage> chatHistory = parseConversation(conversation);
		chatHistory.add(ChatMessage.UserMessage.of(request.getPrompt()));

		conversation.addMessage(new ConversationMessage(MessageSenderType.USER, request.getPrompt()));
		conversationRepository.save(conversation);

		// Build request with existing history
		ChatRequest chatRequest = chatRequestBase
				.messages(chatHistory)
				.stream(true)
				.build();

		return outputStream -> {
			try (Stream<Chat> stream = openAI.chatCompletions().createStream(chatRequest).join()) {
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
							throw new UncheckedIOException("Error writing to output stream", e);
						}
					}
				});

				// Save the complete assistant response to conversation
				if (!response.isEmpty()) {
					conversation.addMessage(new ConversationMessage(MessageSenderType.ASSISTANT, response.toString()));
					conversationRepository.save(conversation);
				}
			} catch (Exception e) {
				throw new RuntimeException("Error processing streaming response", e);
			}
		};
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
