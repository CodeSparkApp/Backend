package de.dhbw.tinf22b6.codespark.api.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
	@JsonProperty("timestamp")
	private LocalDateTime timestamp;

	@JsonProperty("status")
	private int status;

	@JsonProperty("error")
	private String error;

	@JsonProperty("message")
	private String message;

	@JsonProperty("path")
	private String path;
}
