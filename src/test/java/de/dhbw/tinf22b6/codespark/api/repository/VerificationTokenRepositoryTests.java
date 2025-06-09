package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VerificationTokenRepositoryTests {
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void testFindByTokenAndType_shouldReturnToken() {
		Account account = new Account("user1", "test@example.com", "pass", null, true);
		accountRepository.save(account);

		VerificationToken token = new VerificationToken(
				"abc123",
				VerificationTokenType.EMAIL_VERIFICATION,
				Instant.now().plusSeconds(3600),
				account
		);
		verificationTokenRepository.save(token);

		Optional<VerificationToken> found = verificationTokenRepository.findByTokenAndType(
				"abc123",
				VerificationTokenType.EMAIL_VERIFICATION
		);

		assertThat(found).isPresent();
		assertThat(found.get().getAccount().getEmail()).isEqualTo("test@example.com");
	}

	@Test
	void testFindByTokenAndType_shouldReturnEmptyWhenNotFound() {
		Optional<VerificationToken> found = verificationTokenRepository.findByTokenAndType(
				"nonexistent",
				VerificationTokenType.PASSWORD_RESET
		);

		assertThat(found).isEmpty();
	}
}
