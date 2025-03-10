package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("type")
	private LessonType type;

	@JsonProperty("data")
	private String data;

	@JsonProperty("chapter_id")
	private UUID chapterId;

	@JsonProperty("next_lesson_id")
	private UUID nextLessonId;

	@JsonProperty("next_previous_id")
	private UUID previousLessonId;
}
