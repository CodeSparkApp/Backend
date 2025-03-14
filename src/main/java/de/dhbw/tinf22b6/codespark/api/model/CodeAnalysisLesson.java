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
public class CodeAnalysisLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String code;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String question;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String sampleSolution;

	public CodeAnalysisLesson(String title, String description, LessonType type, Chapter chapter,
							  Lesson nextLesson, Lesson previousLesson, String code, String question, String sampleSolution) {
		super(title, description, type, chapter, nextLesson, previousLesson);
		this.code = code;
		this.question = question;
		this.sampleSolution = sampleSolution;
	}
}
