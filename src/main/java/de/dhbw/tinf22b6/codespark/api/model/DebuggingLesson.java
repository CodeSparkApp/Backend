package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DebuggingLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String faultyCode;

	@Column(nullable = false)
	private String expectedOutput;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String sampleSolution;

	public DebuggingLesson(String title, String description, LessonType type, Chapter chapter,
						   Lesson nextLesson, Lesson previousLesson, String faultyCode, String expectedOutput, String sampleSolution) {
		super(title, description, type, chapter, nextLesson, previousLesson);
		this.faultyCode = faultyCode;
		this.expectedOutput = expectedOutput;
		this.sampleSolution = sampleSolution;
	}
}
