package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FillBlanksLessonSubmitRequest extends LessonSubmitRequest {
	@JsonProperty("solutions")
	private List<String> solutions;

	public FillBlanksLessonSubmitRequest(LessonType type, List<String> solutions) {
		super(type);
		this.solutions = solutions;
	}
}
