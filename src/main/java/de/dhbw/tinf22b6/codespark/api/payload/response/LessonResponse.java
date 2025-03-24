package de.dhbw.tinf22b6.codespark.api.payload.response;

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
public abstract class LessonResponse {
	@JsonProperty("id")
	private UUID id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("type")
	private LessonType type;

	@JsonProperty("chapter_id")
	private UUID chapterId;
}

