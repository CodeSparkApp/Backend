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
public class DebuggingLessonResponse extends LessonResponse {
	@JsonProperty("faulty_code")
	private String faultyCode;

	@JsonProperty("expected_output")
	private String expectedOutput;

	public DebuggingLessonResponse(UUID id, String title, String description, LessonType type,
								   UUID chapterId, String faultyCode, String expectedOutput) {
		super(id, title, description, type, chapterId);
		this.faultyCode = faultyCode;
		this.expectedOutput = expectedOutput;
	}
}
