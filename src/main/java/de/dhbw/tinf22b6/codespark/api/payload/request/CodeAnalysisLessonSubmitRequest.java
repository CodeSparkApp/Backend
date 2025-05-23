package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CodeAnalysisLessonSubmitRequest extends LessonSubmitRequest {
	@JsonProperty("solution")
	private String solution;

	public CodeAnalysisLessonSubmitRequest(LessonType type, String solution) {
		super(type);
		this.solution = solution;
	}
}
