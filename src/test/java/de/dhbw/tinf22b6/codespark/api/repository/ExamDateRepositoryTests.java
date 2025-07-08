package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.ExamDate;
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
class ExamDateRepositoryTests {
	@Autowired
	private ExamDateRepository examDateRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void testFindByAccount_shouldReturnExamDate() {
		Account account = new Account("student1", "student1@example.com", "password",
				true, Collections.emptySet(), LocalDateTime.now(), LocalDateTime.now());
		account = accountRepository.save(account);

		ExamDate examDate = new ExamDate(LocalDateTime.of(2025, 7, 15, 10, 0));
		examDate.setAccount(account);
		examDateRepository.save(examDate);

		Optional<ExamDate> result = examDateRepository.findByAccount(account);

		assertThat(result).isPresent();
		assertThat(result.get().getDate()).isEqualTo(LocalDateTime.of(2025, 7, 15, 10, 0));
		assertThat(result.get().getAccount().getUsername()).isEqualTo("student1");
	}
}
