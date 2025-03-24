package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.ExamDateUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ExamDateResponse;

public interface ExamDateService {
	ExamDateResponse getExamDate(Account account);
	void updateExamDate(ExamDateUpdateRequest request, Account account);
}
