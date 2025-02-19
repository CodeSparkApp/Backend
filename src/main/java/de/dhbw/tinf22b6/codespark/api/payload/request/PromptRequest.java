package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptRequest {
	@JsonProperty("user_id")
	private Long userId;

	@JsonProperty("prompt")
	private String prompt;

	public PromptRequest() {}

	public PromptRequest(Long userId, String prompt) {
		this.userId = userId;
		this.prompt = prompt;
	}
}
