package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class MultipleChoiceLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String question;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "multiple_choice_lesson_options",
			joinColumns = @JoinColumn(name = "lesson_id")
	)
	@Column(name = "option", nullable = false)
	@OrderColumn(name = "options_index")
	private List<String> options;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "multiple_choice_lesson_solutions",
			joinColumns = @JoinColumn(name = "lesson_id")
	)
	@Column(name = "solution", nullable = false)
	@OrderColumn(name = "solution_index")
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
