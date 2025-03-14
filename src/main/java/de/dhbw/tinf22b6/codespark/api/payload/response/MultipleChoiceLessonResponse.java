package de.dhbw.tinf22b6.codespark.api.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MultipleChoiceLessonResponse extends LessonResponse {
	@JsonProperty("question")
	private String question;

	@JsonProperty("options")
	private List<String> options;

	public MultipleChoiceLessonResponse(UUID id, String title, String description, LessonType type,
									  	String question, List<String> options) {
		super(id, title, description, type);
		this.question = question;
		this.options = options;
	}
}
