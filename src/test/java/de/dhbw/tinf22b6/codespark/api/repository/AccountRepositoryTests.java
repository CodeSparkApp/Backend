package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Account;
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
class AccountRepositoryTests {
	@Autowired
	private AccountRepository accountRepository;

	@Test
	void testFindByUsername_shouldReturnAccount() {
		Account account = new Account("john_doe", "john@example.com", "hashed_pwd",
				true, Collections.emptySet(), LocalDateTime.now(), LocalDateTime.now());
		accountRepository.save(account);

		Optional<Account> result = accountRepository.findByUsername("john_doe");

		assertThat(result).isPresent();
		assertThat(result.get().getEmail()).isEqualTo("john@example.com");
	}

	@Test
	void findByEmail_shouldReturnAccount() {
		Account account = new Account("jane_doe", "jane@example.com", "hashed_pwd",
				false, Collections.emptySet(), LocalDateTime.now(), LocalDateTime.now());
		accountRepository.save(account);

		Optional<Account> result = accountRepository.findByEmail("jane@example.com");

		assertThat(result).isPresent();
		assertThat(result.get().getUsername()).isEqualTo("jane_doe");
	}

	@Test
	void findByUsername_shouldReturnEmpty() {
		Optional<Account> result = accountRepository.findByUsername("unknown_user");
		assertThat(result).isEmpty();
	}

	@Test
	void findByEmail_shouldReturnEmpty() {
		Optional<Account> result = accountRepository.findByEmail("unknown@example.com");
		assertThat(result).isEmpty();
	}
}
