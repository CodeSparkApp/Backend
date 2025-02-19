package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
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
	public ResponseEntity<String> processPrompt(@RequestBody PromptRequest request) {
		String response = assistantService.processPrompt(request);

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(response);
	}

	@PostMapping(value = "/prompt-stream")
	public ResponseEntity<StreamingResponseBody> processPromptStream(@RequestBody PromptRequest request) {
		StreamingResponseBody responseBody = assistantService.processPromptStream(request);

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(responseBody);
	}
}
