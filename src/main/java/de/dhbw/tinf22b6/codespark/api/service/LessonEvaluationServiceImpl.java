package de.dhbw.tinf22b6.codespark.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import org.springframework.stereotype.Service;

@Service
public class LessonEvaluationServiceImpl implements LessonEvaluationService {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean evaluateLesson(Lesson lesson, LessonSubmitRequest request) {
		return true;
	}
}
