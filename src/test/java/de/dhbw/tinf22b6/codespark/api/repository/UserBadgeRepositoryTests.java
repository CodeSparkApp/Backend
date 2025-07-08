package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.BadgeType;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Badge;
import de.dhbw.tinf22b6.codespark.api.model.UserBadge;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserBadgeRepositoryTests {
	@Autowired
	private UserBadgeRepository userBadgeRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BadgeRepository badgeRepository;

	@Test
	void testFindByAccountAndBadge_shouldReturnUserBadge() {
		Account account = new Account("user1", "user1@example.com", "secret",
				true, Collections.emptySet(), LocalDateTime.now(),  LocalDateTime.now());
		account = accountRepository.save(account);

		Badge badge = new Badge("First Win", "Earned after completing a lesson", BadgeType.FIRST_LESSON_COMPLETED, "icon.png");
		badge = badgeRepository.save(badge);

		UserBadge userBadge = new UserBadge(account, badge, LocalDateTime.of(2025, 6, 1, 10, 0));
		userBadgeRepository.save(userBadge);

		Optional<UserBadge> result = userBadgeRepository.findByAccountAndBadge(account, badge);

		assertThat(result).isPresent();
		assertThat(result.get().getReceiveDate()).isEqualTo(LocalDateTime.of(2025, 6, 1, 10, 0));
	}

	@Test
	void testExistsByAccountAndBadge_shouldReturnTrue() {
		Account account = new Account("user2", "user2@example.com", "pass",
				true, Collections.emptySet(), LocalDateTime.now(),  LocalDateTime.now());
		account = accountRepository.save(account);

		Badge badge = new Badge("Achiever", "Earned after completing a chapter", BadgeType.FIRST_CHAPTER_COMPLETED, "icon2.png");
		badge = badgeRepository.save(badge);

		UserBadge userBadge = new UserBadge(account, badge, LocalDateTime.now());
		userBadgeRepository.save(userBadge);

		boolean exists = userBadgeRepository.existsByAccountAndBadge(account, badge);

		assertThat(exists).isTrue();
	}

	@Test
	void testExistsByAccountAndBadge_shouldReturnFalseWhenNotExists() {
		Account account = accountRepository.save(new Account("user3", "user3@example.com", "pw",
				true, Collections.emptySet(), LocalDateTime.now(),  LocalDateTime.now()));
		Badge badge = badgeRepository.save(new Badge("Unobtainable", "Never awarded", BadgeType.ALL_CHAPTERS_COMPLETED, "icon3.png"));

		boolean exists = userBadgeRepository.existsByAccountAndBadge(account, badge);

		assertThat(exists).isFalse();
	}
}
