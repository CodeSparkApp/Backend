package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Chapter {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@OneToOne
	@JoinColumn(name = "first_lesson_id")
	private Lesson firstLesson;

	@OneToOne
	@JoinColumn(name = "next_chapter_id")
	private Chapter nextChapter;

	public Chapter(String title, String description, Lesson firstLesson, Chapter nextChapter) {
		this.title = title;
		this.description = description;
		this.firstLesson = firstLesson;
		this.nextChapter = nextChapter;
	}
}
