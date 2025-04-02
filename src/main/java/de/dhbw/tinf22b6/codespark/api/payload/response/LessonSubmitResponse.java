package de.dhbw.tinf22b6.codespark.api.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonEvaluationState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonSubmitResponse {
	@JsonProperty("evaluation_result")
	private LessonEvaluationState evaluationResult;

	@JsonProperty("explanation")
	private String explanation;

	@JsonProperty("next_lesson_id")
	private UUID nextLesson;
}
