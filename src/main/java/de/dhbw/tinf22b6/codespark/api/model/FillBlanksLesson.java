package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FillBlanksLesson extends Lesson {
	@Column(columnDefinition = "TEXT", nullable = false)
	private String templateCode;

	@Column(nullable = false)
	private String expectedOutput;

	@ElementCollection
	@Column(nullable = false)
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
