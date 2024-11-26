package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.service.interfaces.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(value = "/api/v1/openai")
public class AssistantController {
	private final AssistantService assistantService;

	public AssistantController(@Autowired AssistantService assistantService) {
		this.assistantService = assistantService;
	}

	@PostMapping("/prompt")
	public String sendPrompt(@RequestBody String prompt) {
		return assistantService.sendPrompt(prompt);
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<StreamingResponseBody> sendPromptStream(@RequestBody String prompt) {
		StreamingResponseBody responseBody = assistantService.sendPromptStream(prompt);

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(responseBody);
	}
}
