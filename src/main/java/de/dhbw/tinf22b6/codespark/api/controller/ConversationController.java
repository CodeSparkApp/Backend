package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.exception.UserNotFoundException;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
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

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/openai")
public class ConversationController {
	private final AccountService accountService;
	private final ConversationService conversationService;

	public ConversationController(@Autowired AccountService accountService,
								  @Autowired ConversationService conversationService) {
		this.accountService = accountService;
		this.conversationService = conversationService;
	}

	@PostMapping("/prompt")
	public ResponseEntity<?> processPrompt(@RequestBody PromptRequest request, Principal principal) {
		try {
			String username = principal.getName();
			UUID userId = accountService.getUserIdByUsername(username);

			String response = conversationService.processPrompt(userId, request);

			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.TEXT_PLAIN)
					.body(response);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(null);
		}
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<StreamingResponseBody> processPromptStream(@RequestBody PromptRequest request, Principal principal) {
		try {
			String username = principal.getName();
			UUID userId = accountService.getUserIdByUsername(username);

			StreamingResponseBody responseBody = conversationService.processPromptStream(userId, request);

			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.TEXT_PLAIN)
					.body(responseBody);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(null);
		}
	}
}
