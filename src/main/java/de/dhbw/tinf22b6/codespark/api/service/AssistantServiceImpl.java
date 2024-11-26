package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.service.interfaces.AssistantService;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
public class AssistantServiceImpl implements AssistantService {
	private final SimpleOpenAI openAI;
	private final ChatRequest.ChatRequestBuilder chatRequestBase;

	public AssistantServiceImpl(@Autowired Environment environment) {
		this.openAI = SimpleOpenAI.builder()
				.apiKey(environment.getRequiredProperty("openai.api.key"))
				.organizationId(environment.getRequiredProperty("openai.api.organization_id"))
				.projectId(environment.getRequiredProperty("openai.api.project_id"))
				.build();

		this.chatRequestBase = ChatRequest.builder()
				.model(environment.getRequiredProperty("openai.model.name"))
				.temperature(0.4)
				.maxCompletionTokens(500);
	}

	@Override
	public String sendPrompt(String prompt) {
		ChatRequest chatRequest = chatRequestBase
				.message(ChatMessage.UserMessage.of(prompt))
				.build();
		CompletableFuture<Chat> futureChat = openAI.chatCompletions().create(chatRequest);
		Chat chatResponse = futureChat.join();
		return chatResponse.firstContent();
	}

	@Override
	public StreamingResponseBody sendPromptStream(String prompt) {
		ChatRequest chatRequest = chatRequestBase
				.message(ChatMessage.UserMessage.of(prompt))
				.stream(true)
				.build();

		return outputStream -> {
			try (Stream<Chat> stream = openAI.chatCompletions().createStream(chatRequest).join()) {
				stream.forEach(chunk -> {
					String message = chunk.getChoices().stream()
							.findFirst()
							.map(choice -> choice.getMessage().getContent())
							.orElse("");
					if (!message.isEmpty()) {
						try {
							outputStream.write(message.getBytes(StandardCharsets.UTF_8));
							outputStream.flush();
						} catch (IOException e) {
							throw new UncheckedIOException("Error writing to output stream", e);
						}
					}
				});
			} catch (Exception e) {
				throw new RuntimeException("Error processing streaming response", e);
			}
		};
	}
}
