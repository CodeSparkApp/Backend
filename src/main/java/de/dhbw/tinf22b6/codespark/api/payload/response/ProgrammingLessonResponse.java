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
public class ProgrammingLessonResponse extends LessonResponse {
	@JsonProperty("problem")
	private String problem;

	@JsonProperty("code")
	private String code;

	public ProgrammingLessonResponse(UUID id, String title, String description, LessonType type,
									 String problem, String code) {
		super(id, title, description, type);
		this.problem = problem;
		this.code = code;
	}
}
