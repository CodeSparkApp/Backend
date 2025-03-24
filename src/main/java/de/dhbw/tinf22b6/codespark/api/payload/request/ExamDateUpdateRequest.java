package de.dhbw.tinf22b6.codespark.api.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamDateUpdateRequest {
	@JsonProperty("exam_date")
	private LocalDateTime examDate;
}
