package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
	@JsonProperty("verification_token")
	private String verificationToken;

	@JsonProperty("password")
	private String password;
}
