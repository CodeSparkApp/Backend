package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.ExamDate;
import de.dhbw.tinf22b6.codespark.api.payload.request.ExamDateUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ExamDateResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ExamDateRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ExamDateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExamDateServiceImplTests {
	private ExamDateRepository examDateRepository;
	private ExamDateService examDateService;

	@BeforeEach
	void setUp() {
		examDateRepository = mock(ExamDateRepository.class);

		examDateService = new ExamDateServiceImpl(examDateRepository);
	}

	@Test
	void getExamDate_shouldReturnDateIfExists() {
		Account account = new Account();
		LocalDateTime expectedDate = LocalDateTime.of(2025, 8, 20, 10, 0);
		ExamDate examDate = new ExamDate();
		examDate.setDate(expectedDate);

		when(examDateRepository.findByAccount(account)).thenReturn(Optional.of(examDate));

		ExamDateResponse response = examDateService.getExamDate(account);

		assertThat(response).isNotNull();
		assertThat(response.getExamDate()).isEqualTo(expectedDate);
	}

	@Test
	void getExamDate_shouldReturnNullIfNotExists() {
		Account account = new Account();

		when(examDateRepository.findByAccount(account)).thenReturn(Optional.empty());

		ExamDateResponse response = examDateService.getExamDate(account);

		assertThat(response).isNotNull();
		assertThat(response.getExamDate()).isNull();
	}

	@Test
	void updateExamDate_shouldUpdateExistingDate() {
		Account account = new Account();
		LocalDateTime newDate = LocalDateTime.of(2025, 10, 1, 9, 0);
		ExamDateUpdateRequest request = new ExamDateUpdateRequest(newDate);
		ExamDate existing = new ExamDate();
		existing.setAccount(account);
		existing.setDate(LocalDateTime.now());

		when(examDateRepository.findByAccount(account)).thenReturn(Optional.of(existing));

		examDateService.updateExamDate(request, account);

		assertThat(existing.getDate()).isEqualTo(newDate);
		assertThat(existing.getAccount()).isEqualTo(account);
		verify(examDateRepository).save(existing);
	}

	@Test
	void updateExamDate_shouldCreateNewIfNotExists() {
		Account account = new Account();
		LocalDateTime newDate = LocalDateTime.of(2025, 12, 15, 14, 30);
		ExamDateUpdateRequest request = new ExamDateUpdateRequest(newDate);

		when(examDateRepository.findByAccount(account)).thenReturn(Optional.empty());

		examDateService.updateExamDate(request, account);

		verify(examDateRepository).save(argThat(saved ->
				saved.getDate().equals(newDate) &&
						saved.getAccount().equals(account)
		));
	}
}
