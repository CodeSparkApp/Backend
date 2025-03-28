package de.dhbw.tinf22b6.codespark.api.payload.request;

import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TheoryLessonSubmitRequest extends LessonSubmitRequest {
	public TheoryLessonSubmitRequest(LessonType type) {
		super(type);
	}
}
