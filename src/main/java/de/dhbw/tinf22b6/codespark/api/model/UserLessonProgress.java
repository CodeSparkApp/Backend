package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserLessonProgress {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Lesson lesson;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LessonProgressState state;

	public UserLessonProgress(Account account, Lesson lesson, LessonProgressState state) {
		this.account = account;
		this.lesson = lesson;
		this.state = state;
	}
}
