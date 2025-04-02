package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.service.dto.LessonEvaluationResult;

public interface LessonEvaluationService {
	LessonEvaluationResult evaluateLesson(Lesson lesson, LessonSubmitRequest request, Account account);
	void skipLesson(Lesson lesson, Account account);
}
