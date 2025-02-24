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
public class LoginRequest {
	@JsonProperty("username_or_email")
	private String usernameOrEmail;

	@JsonProperty("password")
	private String password;
}
