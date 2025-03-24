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
public class TheoryLessonResponse extends LessonResponse {
	@JsonProperty("text")
	private String text;

	public TheoryLessonResponse(UUID id, String title, String description, LessonType type,
								UUID chapterId, String text) {
		super(id, title, description, type, chapterId);
		this.text = text;
	}
}
