package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(value = "/api/v1/openai")
public class ConversationController {
	private final ConversationService conversationService;

	public ConversationController(@Autowired ConversationService conversationService) {
		this.conversationService = conversationService;
	}

	@PostMapping("/prompt")
	public ResponseEntity<?> processPrompt(@RequestBody PromptRequest request) {
		String response = conversationService.processPrompt(request);

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body(response);
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<?> processPromptStream(@RequestBody PromptRequest request) {
		StreamingResponseBody responseBody = conversationService.processPromptStream(request);

		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body(responseBody);
	}
}
