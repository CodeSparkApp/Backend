package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lesson {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private LessonType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Chapter chapter;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "next_lesson_id")
	private Lesson nextLesson;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "previous_lesson_id")
	private Lesson previousLesson;

	public Lesson(String title, String description, LessonType type, Chapter chapter,
				  Lesson nextLesson, Lesson previousLesson) {
		this.title = title;
		this.description = description;
		this.type = type;
		this.chapter = chapter;
		this.nextLesson = nextLesson;
		this.previousLesson = previousLesson;
	}
}
