package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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

	@JsonProperty("chapter_id")
	private UUID chapterId;

	@JsonProperty("next_lesson_id")
	private UUID nextLessonId;

	@JsonProperty("next_previous_id")
	private UUID previousLessonId;

	// Fields for specific lesson types

	@JsonProperty("text")
	private String text; // THEORY

	@JsonProperty("problem")
	private String problem; // PROGRAMMING

	@JsonProperty("code")
	private String code; // PROGRAMMING / CODE_ANALYSIS

	@JsonProperty("question")
	private String question; // MULTIPLE_CHOICE / CODE_ANALYSIS

	@JsonProperty("options")
	private List<String> options; // MULTIPLE_CHOICE

	@JsonProperty("faulty_code")
	private String faultyCode; // DEBUGGING

	@JsonProperty("expected_output")
	private String expectedOutput; // DEBUGGING / FILL_BLANKS

	@JsonProperty("template_code")
	private String templateCode; // FILL_BLANKS

	@JsonProperty("sample_solution")
	private String sampleSolution; // PROGRAMMING / CODE_ANALYSIS / DEBUGGING

	@JsonProperty("correct_blanks")
	private List<String> correctBlanks; // FILL_BLANKS

	@JsonProperty("correct_options")
	private List<Integer> correctOptions; // MULTIPLE_CHOICE
}
