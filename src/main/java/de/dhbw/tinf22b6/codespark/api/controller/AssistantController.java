package de.dhbw.tinf22b6.codespark.api.controller;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/openai")
public class AssistantController {
	private final SimpleOpenAI openAI;

	public AssistantController() {
		this.openAI = SimpleOpenAI.builder()
				.apiKey(System.getenv("CODESPARK_OPENAI_KEY"))
				.organizationId(System.getenv("CODESPARK_ORGANIZATION_ID"))
				.projectId(System.getenv("CODESPARK_PROJECT_ID"))
				.build();
	}

	@PostMapping("/prompt")
	public String sendPrompt(@RequestBody String prompt) {
		var chatRequest = ChatRequest.builder()
				.model("gpt-4o-mini")
				.message(ChatMessage.UserMessage.of(prompt))
				.temperature(0.0)
				.maxCompletionTokens(300)
				.build();
		var futureChat = openAI.chatCompletions().create(chatRequest);
		var chatResponse = futureChat.join();
		return chatResponse.firstContent();
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<StreamingResponseBody> sendPromptStream(@RequestBody String prompt) {
		var chatRequest = ChatRequest.builder()
				.model("gpt-4o-mini")
				.message(ChatMessage.UserMessage.of(prompt))
				.temperature(0.0)
				.maxCompletionTokens(300)
				.stream(true)
				.build();

		StreamingResponseBody responseBody = outputStream -> {
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

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(responseBody);
	}
}
