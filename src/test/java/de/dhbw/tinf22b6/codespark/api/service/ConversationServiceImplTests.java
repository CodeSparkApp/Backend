package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.MessageSenderType;
import de.dhbw.tinf22b6.codespark.api.exception.ChatStreamingException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import de.dhbw.tinf22b6.codespark.api.model.ConversationMessage;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.repository.ConversationRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
import io.github.sashirestela.openai.OpenAI;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConversationServiceImplTests {
	private SimpleOpenAI simpleOpenAI;
	private Environment env;
	private ConversationRepository conversationRepository;
	private ConversationService conversationService;

	@BeforeEach
	void setUp() {
		simpleOpenAI = mock(SimpleOpenAI.class);
		env = mock(Environment.class);
		conversationRepository = mock(ConversationRepository.class);

		conversationService = new ConversationServiceImpl(simpleOpenAI, env, conversationRepository);
	}

	@Test
	void processPrompt_shouldReturnChatResponse_andSaveConversation() {
		Account account = new Account();
		PromptRequest request = new PromptRequest("Hello?");
		Conversation conversation = new Conversation(account);

		when(conversationRepository.findByAccount(account)).thenReturn(Optional.of(conversation));
		when(env.getRequiredProperty("openai.model.name")).thenReturn("gpt-test");

		Chat mockChat = mock(Chat.class);
		when(mockChat.firstContent()).thenReturn("Hi there!");

		OpenAI.ChatCompletions chatModel = mock(OpenAI.ChatCompletions.class);
		when(simpleOpenAI.chatCompletions()).thenReturn(chatModel);
		when(chatModel.create(any(ChatRequest.class))).thenReturn(CompletableFuture.completedFuture(mockChat));

		String result = conversationService.processPrompt(account, request);

		assertEquals("Hi there!", result);
		verify(conversationRepository, times(1)).save(any(Conversation.class));
	}

	@Test
	void processPrompt_shouldCreateNewConversationIfNoneExists() {
		Account account = new Account();
		PromptRequest request = new PromptRequest("Hello?");
		when(conversationRepository.findByAccount(account)).thenReturn(Optional.empty());
		when(env.getRequiredProperty("openai.model.name")).thenReturn("gpt-test");

		Chat mockChat = mock(Chat.class);
		when(mockChat.firstContent()).thenReturn("Hi!");
		OpenAI.ChatCompletions chatModel = mock(OpenAI.ChatCompletions.class);
		when(simpleOpenAI.chatCompletions()).thenReturn(chatModel);
		when(chatModel.create(any(ChatRequest.class))).thenReturn(CompletableFuture.completedFuture(mockChat));

		String response = conversationService.processPrompt(account, request);

		assertEquals("Hi!", response);
		verify(conversationRepository).save(any(Conversation.class));
	}

	@Test
	void processPromptStream_shouldStreamChunks_andSaveResponse() throws Exception {
		Account account = new Account();
		PromptRequest request = new PromptRequest("Explain polymorphism.");
		Conversation conversation = new Conversation(account);

		when(conversationRepository.findByAccount(account)).thenReturn(Optional.of(conversation));
		when(env.getRequiredProperty("openai.model.name")).thenReturn("gpt-stream");

		Chat chunk1 = mock(Chat.class);
		ChatMessage.ResponseMessage responseMessage1 = new ChatMessage.ResponseMessage();
		responseMessage1.setContent("Poly");
		Chat.Choice choice1 = new Chat.Choice();
		choice1.setMessage(responseMessage1);
		when(chunk1.getChoices()).thenReturn(List.of(choice1));
		Chat chunk2 = mock(Chat.class);
		ChatMessage.ResponseMessage responseMessage2 = new ChatMessage.ResponseMessage();
		responseMessage2.setContent("morphism");
		Chat.Choice choice2 = new Chat.Choice();
		choice2.setMessage(responseMessage2);
		when(chunk2.getChoices()).thenReturn(List.of(choice2));

		OpenAI.ChatCompletions chatModel = mock(OpenAI.ChatCompletions.class);
		when(simpleOpenAI.chatCompletions()).thenReturn(chatModel);
		when(chatModel.createStream(any(ChatRequest.class)))
				.thenReturn(CompletableFuture.completedFuture(Stream.of(chunk1, chunk2)));

		StreamingResponseBody stream = conversationService.processPromptStream(account, request);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		stream.writeTo(outputStream);

		String response = outputStream.toString(StandardCharsets.UTF_8);
		assertTrue(response.contains("Poly"));
		assertTrue(response.contains("morphism"));
		verify(conversationRepository, atLeast(2)).save(any(Conversation.class));
	}

	@Test
	void processPromptStream_shouldThrowChatStreamingException_onIOException() throws Exception {
		Account account = mock(Account.class);
		PromptRequest request = new PromptRequest("test prompt");
		Conversation conversation = new Conversation(account);

		when(conversationRepository.findByAccount(account)).thenReturn(Optional.of(conversation));
		when(env.getRequiredProperty("openai.model.name")).thenReturn("gpt-test");

		Chat chatChunk = mock(Chat.class);
		ChatMessage.ResponseMessage responseMessage = new ChatMessage.ResponseMessage();
		responseMessage.setContent("Hello World!");
		Chat.Choice chatChoice = new Chat.Choice();
		chatChoice.setMessage(responseMessage);
		when(chatChunk.getChoices()).thenReturn(List.of(chatChoice));

		OpenAI.ChatCompletions chatModel = mock(OpenAI.ChatCompletions.class);
		when(simpleOpenAI.chatCompletions()).thenReturn(chatModel);
		when(chatModel.createStream(any(ChatRequest.class)))
				.thenReturn(CompletableFuture.completedFuture(Stream.of(chatChunk)));

		StreamingResponseBody responseBody = conversationService.processPromptStream(account, request);

		OutputStream outputStream = mock(OutputStream.class);
		doThrow(new IOException("Broken pipe")).when(outputStream).write(any(byte[].class));

		assertThatThrownBy(() -> responseBody.writeTo(outputStream))
				.isInstanceOf(ChatStreamingException.class)
				.hasMessageContaining("Something went wrong while generating a response");
	}

	@Test
	void processPromptStream_shouldThrowChatStreamingException_onStreamFailure() {
		Account account = mock(Account.class);
		PromptRequest request = new PromptRequest("trigger error");
		Conversation conversation = new Conversation(account);

		when(conversationRepository.findByAccount(account)).thenReturn(Optional.of(conversation));
		when(env.getRequiredProperty("openai.model.name")).thenReturn("gpt-test");

		OpenAI.ChatCompletions chatModel = mock(OpenAI.ChatCompletions.class);
		when(simpleOpenAI.chatCompletions()).thenReturn(chatModel);

		when(simpleOpenAI.chatCompletions().createStream(any(ChatRequest.class)))
				.thenThrow(new RuntimeException("OpenAI down"));

		StreamingResponseBody responseBody = conversationService.processPromptStream(account, request);

		assertThatThrownBy(() -> responseBody.writeTo(new ByteArrayOutputStream()))
				.isInstanceOf(ChatStreamingException.class)
				.hasMessageContaining("Something went wrong while generating a response");
	}

	@Test
	void parseConversation_shouldParseAllMessageTypes() {
		Conversation conversation = new Conversation(mock(Account.class));
		conversation.addMessage(new ConversationMessage(MessageSenderType.USER, "user msg"));
		conversation.addMessage(new ConversationMessage(MessageSenderType.ASSISTANT, "assistant msg"));
		conversation.addMessage(new ConversationMessage(MessageSenderType.SYSTEM, "system msg"));

		List<ChatMessage> messages = ReflectionTestUtils.invokeMethod(conversationService, "parseConversation", conversation);

		assertThat(messages).hasSize(3);
		assertThat(messages.get(0)).isInstanceOf(ChatMessage.UserMessage.class);
		assertThat(messages.get(1)).isInstanceOf(ChatMessage.AssistantMessage.class);
		assertThat(messages.get(2)).isInstanceOf(ChatMessage.SystemMessage.class);
	}
}
