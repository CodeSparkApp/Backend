package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
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
	public ResponseEntity<String> processPrompt(@RequestBody PromptRequest request, Principal principal) {
		String username = principal.getName();
		UUID accountId = accountService.getAccountIdByUsername(username);

		String response = conversationService.processPrompt(accountId, request);

		return ResponseEntity.ok().body(response);
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<StreamingResponseBody> processPromptStream(@RequestBody PromptRequest request, Principal principal) {
		String username = principal.getName();
		UUID accountId = accountService.getAccountIdByUsername(username);

		StreamingResponseBody response = conversationService.processPromptStream(accountId, request);

		return ResponseEntity.ok().body(response);
	}
}
