package de.dhbw.tinf22b6.codespark.api.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("refresh_token")
	private String refreshToken;
}
