package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChapterRepositoryTests {
	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private LessonRepository lessonRepository;

	@Test
	void testFindByNextChapterId_shouldReturnCorrectChapter() {
		Chapter chapter1 = new Chapter("Chapter 1", "Description 1", null, null);
		Chapter chapter2 = new Chapter("Chapter 2", "Description 2", null, null);

		chapter2 = chapterRepository.save(chapter2);

		chapter1.setNextChapter(chapter2);
		chapterRepository.save(chapter1);

		Optional<Chapter> result = chapterRepository.findByNextChapterId(chapter2.getId());

		assertThat(result).isPresent();
		assertThat(result.get().getTitle()).isEqualTo("Chapter 1");
	}
}
