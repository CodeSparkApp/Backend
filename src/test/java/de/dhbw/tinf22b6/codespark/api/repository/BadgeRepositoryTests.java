package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.BadgeType;
import de.dhbw.tinf22b6.codespark.api.model.Badge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BadgeRepositoryTests {
	@Autowired
	private BadgeRepository badgeRepository;

	@Test
	void testFindByType_shouldReturnBadge() {
		Badge badge = new Badge("First Lesson", "Awarded for completing your first lesson",
				BadgeType.FIRST_LESSON_COMPLETED, "first_lesson_icon.png");
		badgeRepository.save(badge);

		Optional<Badge> result = badgeRepository.findByType(BadgeType.FIRST_LESSON_COMPLETED);

		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("First Lesson");
	}

	@Test
	void testFindByType_shouldReturnEmpty() {
		Optional<Badge> result = badgeRepository.findByType(BadgeType.FIRST_LESSON_COMPLETED);
		assertThat(result).isEmpty();
	}
}
