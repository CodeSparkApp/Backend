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
public class FillBlanksLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String templateCode;

	@Column(nullable = false)
	private String expectedOutput;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "fill_blanks_lesson_solutions",
			joinColumns = @JoinColumn(name = "lesson_id")
	)
	@Column(name = "solution", nullable = false)
	@OrderColumn(name = "solution_index")
	private List<String> solutions;

	public FillBlanksLesson(String title, String description, LessonType type, Chapter chapter,
							Lesson nextLesson, Lesson previousLesson, String templateCode, String expectedOutput,
							List<String> solutions) {
		super(title, description, type, chapter, nextLesson, previousLesson);
		this.templateCode = templateCode;
		this.expectedOutput = expectedOutput;
		this.solutions = solutions;
	}
}
