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
public class CodeAnalysisLessonResponse extends LessonResponse {
	@JsonProperty("code")
	private String code;

	@JsonProperty("question")
	private String question;

	public CodeAnalysisLessonResponse(UUID id, String title, String description, LessonType type,
									  UUID chapterId, String code, String question) {
		super(id, title, description, type, chapterId);
		this.code = code;
		this.question = question;
	}
}
