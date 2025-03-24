package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.ExamDate;
import de.dhbw.tinf22b6.codespark.api.payload.request.ExamDateUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ExamDateResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ExamDateRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ExamDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExamDateServiceImpl implements ExamDateService {
	private final ExamDateRepository examDateRepository;

	public ExamDateServiceImpl(@Autowired ExamDateRepository examDateRepository) {
		this.examDateRepository = examDateRepository;
	}

	@Override
	public ExamDateResponse getExamDate(Account account) {
		LocalDateTime date = examDateRepository.findByAccount(account)
				.map(ExamDate::getDate)
				.orElse(null);

		return new ExamDateResponse(date);
	}

	@Override
	public void updateExamDate(ExamDateUpdateRequest request, Account account) {
		ExamDate examDate = examDateRepository.findByAccount(account)
				.orElse(new ExamDate());

		examDate.setDate(request.getExamDate());
		examDate.setAccount(account);

		examDateRepository.save(examDate);
	}
}
