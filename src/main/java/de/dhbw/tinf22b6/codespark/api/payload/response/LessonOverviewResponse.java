package de.dhbw.tinf22b6.codespark.api.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonOverviewResponse {
	@JsonProperty("chapter_title")
	private String chapterTitle;

	@JsonProperty("chapter_description")
	private String chapterDescription;

	@JsonProperty("lessons")
	private List<LessonItemResponse> lessons;
}
