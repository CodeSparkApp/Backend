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
public class TheoryLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String text;

	public TheoryLesson(String title, String description, LessonType type, Chapter chapter,
						Lesson nextLesson, Lesson previousLesson, String text) {
		super(title, description, type, chapter, nextLesson, previousLesson);
		this.text = text;
	}
}
