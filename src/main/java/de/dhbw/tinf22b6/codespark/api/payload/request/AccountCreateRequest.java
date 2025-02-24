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
public class AccountCreateRequest {
	@JsonProperty("username")
	private String username;

	@JsonProperty("email")
	private String email;

	@JsonProperty("password")
	private String password;
}
