package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface ConversationService {
	String processPrompt(Account account, PromptRequest request);
	StreamingResponseBody processPromptStream(Account account, PromptRequest request);
}
