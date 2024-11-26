package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AssistantService {
	String sendPrompt(@RequestBody String prompt);
	StreamingResponseBody sendPromptStream(@RequestBody String prompt);
}
