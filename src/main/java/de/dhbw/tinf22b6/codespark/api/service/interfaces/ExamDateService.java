package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.ExamDateUpdateRequest;

public interface ExamDateService {
	void updateExamDate(ExamDateUpdateRequest request, Account account);
}
