package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	public ResponseEntity<String> processPrompt(@RequestBody PromptRequest request,
												@AuthenticationPrincipal Account account) {
		String response = conversationService.processPrompt(account, request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<StreamingResponseBody> processPromptStream(@RequestBody PromptRequest request,
																	 @AuthenticationPrincipal Account account) {
		StreamingResponseBody response = conversationService.processPromptStream(account, request);
		return ResponseEntity.ok().body(response);
	}
}
