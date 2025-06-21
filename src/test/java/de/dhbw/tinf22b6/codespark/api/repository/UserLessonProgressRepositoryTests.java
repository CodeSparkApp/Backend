package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.model.TheoryLesson;
import de.dhbw.tinf22b6.codespark.api.model.UserLessonProgress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserLessonProgressRepositoryTests {
	@Autowired
	private UserLessonProgressRepository progressRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private LessonRepository lessonRepository;

	@Test
	void testFindByAccountAndLesson_shouldReturnProgress() {
		Account account = accountRepository.save(new Account("testUser", "user@example.com", "pw", UserRoleType.USER,
				true, LocalDateTime.now(),  LocalDateTime.now()));
		Chapter chapter = chapterRepository.save(new Chapter("Chapter 1", "Intro", null, null));

		TheoryLesson lesson = lessonRepository.save(new TheoryLesson("L1", "Theory", LessonType.THEORY,chapter, null, null, "Sample text"));
		progressRepository.save(new UserLessonProgress(account, lesson, LessonProgressState.ATTEMPTED));

		Optional<UserLessonProgress> result = progressRepository.findByAccountAndLesson(account, lesson);

		assertThat(result).isPresent();
		assertThat(result.get().getState()).isEqualTo(LessonProgressState.ATTEMPTED);
	}

	@Test
	void testFindByAccountAndState_shouldReturnCorrectList() {
		Account account = accountRepository.save(new Account("testUser2", "user2@example.com", "pw", UserRoleType.USER,
				true, LocalDateTime.now(),  LocalDateTime.now()));
		Chapter chapter = chapterRepository.save(new Chapter("Chapter 2", "Intro 2", null, null));

		TheoryLesson lesson1 = lessonRepository.save(new TheoryLesson("L2", "Theory 2", LessonType.THEORY, chapter, null, null, "Text 1"));
		TheoryLesson lesson2 = lessonRepository.save(new TheoryLesson("L3", "Theory 3", LessonType.THEORY, chapter, null, null, "Text 2"));

		progressRepository.save(new UserLessonProgress(account, lesson1, LessonProgressState.SOLVED));
		progressRepository.save(new UserLessonProgress(account, lesson2, LessonProgressState.SOLVED));

		List<UserLessonProgress> result = progressRepository.findByAccountAndState(account, LessonProgressState.SOLVED);

		assertThat(result).hasSize(2);
		assertThat(result).allMatch(p -> p.getState() == LessonProgressState.SOLVED);
	}

	@Test
	void testFindByAccountAndLesson_shouldReturnEmptyWhenNone() {
		Account account = accountRepository.save(new Account("user3", "user3@example.com", "pw", UserRoleType.USER,
				true, LocalDateTime.now(),  LocalDateTime.now()));
		Chapter chapter = chapterRepository.save(new Chapter("Chapter X", "X", null, null));
		TheoryLesson lesson = lessonRepository.save(new TheoryLesson("L4", "No progress", LessonType.THEORY, chapter, null, null, "Empty"));

		Optional<UserLessonProgress> result = progressRepository.findByAccountAndLesson(account, lesson);

		assertThat(result).isEmpty();
	}
}
