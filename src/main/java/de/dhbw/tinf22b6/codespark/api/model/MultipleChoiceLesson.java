package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MultipleChoiceLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String question;

	@Column(nullable = false)
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> options;

	@Column(nullable = false)
	@ElementCollection(fetch = FetchType.EAGER)
	private List<Integer> solutions;

	public MultipleChoiceLesson(String title, String description, LessonType type, Chapter chapter,
								Lesson nextLesson, Lesson previousLesson, String question,
								List<String> options, List<Integer> solutions) {
		super(title, description, type, chapter, nextLesson, previousLesson);
		this.question = question;
		this.options = options;
		this.solutions = solutions;
	}
}
