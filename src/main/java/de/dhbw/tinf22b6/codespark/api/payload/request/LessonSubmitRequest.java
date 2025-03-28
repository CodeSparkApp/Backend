package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXISTING_PROPERTY,
		property = "type",
		visible = true
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = CodeAnalysisLessonSubmitRequest.class, name = "CODE_ANALYSIS"),
		@JsonSubTypes.Type(value = DebuggingLessonSubmitRequest.class, name = "DEBUGGING"),
		@JsonSubTypes.Type(value = FillBlanksLessonSubmitRequest.class, name = "FILL_BLANKS"),
		@JsonSubTypes.Type(value = MultipleChoiceLessonSubmitRequest.class, name = "MULTIPLE_CHOICE"),
		@JsonSubTypes.Type(value = ProgrammingLessonSubmitRequest.class, name = "PROGRAMMING"),
		@JsonSubTypes.Type(value = TheoryLessonSubmitRequest.class, name = "THEORY")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class LessonSubmitRequest {
	@JsonProperty("type")
	private LessonType type;
}
