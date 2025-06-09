package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.projection.ChapterProgressProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ChapterProgressRepositoryTests {
	@Autowired
	private ChapterProgressRepository chapterProgressRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void testFindProgressByAccountId_shouldReturnEmptyListWhenNoData() {
		UUID fakeAccountId = UUID.randomUUID();
		List<ChapterProgressProjection> result = chapterProgressRepository.findProgressByAccountId(fakeAccountId);

		assertThat(result).isEmpty();
	}

	@Test
	@Sql("/sql/setup_chapter_progress_view.sql")
	void testFindProgressByAccountId_shouldReturnCorrectProgress() {
		UUID accountId = UUID.fromString("00000000-0000-0000-0000-000000000001");

		List<ChapterProgressProjection> progress = chapterProgressRepository.findProgressByAccountId(accountId);

		assertThat(progress).hasSize(1);
		assertThat(progress.getFirst().getChapterId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
		assertThat(progress.getFirst().getProgress()).isEqualTo(0.75f);
	}
}

