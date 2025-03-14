package de.dhbw.tinf22b6.codespark.api.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class FillBlanksLessonResponse extends LessonResponse {
	@JsonProperty("template_code")
	private String templateCode;

	@JsonProperty("expected_output")
	private String expectedOutput;

	public FillBlanksLessonResponse(UUID id, String title, String description, LessonType type,
									String templateCode, String expectedOutput) {
		super(id, title, description, type);
		this.templateCode = templateCode;
		this.expectedOutput = expectedOutput;
	}
}
