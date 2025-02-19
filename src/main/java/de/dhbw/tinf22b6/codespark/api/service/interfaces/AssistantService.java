package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface AssistantService {
	String processPrompt(@RequestBody PromptRequest request);
	StreamingResponseBody processPromptStream(@RequestBody PromptRequest request);
}
