package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.UUID;

public interface ConversationService {
	String processPrompt(UUID accountId, PromptRequest request);
	StreamingResponseBody processPromptStream(UUID accountId, PromptRequest request);
}
