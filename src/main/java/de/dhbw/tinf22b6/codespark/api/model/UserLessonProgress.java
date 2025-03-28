package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserLessonProgress {
	@EmbeddedId
	private UserLessonProgressId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("accountId")
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("lessonId")
	@JoinColumn(name = "lesson_id", nullable = false)
	private Lesson lesson;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LessonProgressState state;

	public UserLessonProgress(Account account, Lesson lesson, LessonProgressState state) {
		this.id = new UserLessonProgressId(account.getId(), lesson.getId());
		this.account = account;
		this.lesson = lesson;
		this.state = state;
	}
}
