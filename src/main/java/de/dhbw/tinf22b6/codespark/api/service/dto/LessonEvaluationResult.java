package de.dhbw.tinf22b6.codespark.api.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonEvaluationResult {
	private String explanation;
	private boolean correct;
}
