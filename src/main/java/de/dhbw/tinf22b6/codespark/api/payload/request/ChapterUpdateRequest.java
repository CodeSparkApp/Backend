package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterUpdateRequest {
	@JsonProperty("first_lesson_id")
	private UUID firstLessonId;

	@JsonProperty("next_chapter_id")
	private UUID nextChapterId;
}
