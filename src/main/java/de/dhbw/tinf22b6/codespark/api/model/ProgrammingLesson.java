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
public class ProgrammingLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String problem;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String code;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String sampleSolution;

	public ProgrammingLesson(String title, String description, LessonType type, Chapter chapter,
							 Lesson nextLesson, Lesson previousLesson, String problem, String code, String sampleSolution) {
		super(title, description, type, chapter, nextLesson, previousLesson);
		this.problem = problem;
		this.code = code;
		this.sampleSolution = sampleSolution;
	}
}
