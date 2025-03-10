package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.LessonCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonSubmitResponse;

import java.util.UUID;

public interface LessonService {
	LessonResponse getLessonById(UUID id);
	LessonSubmitResponse evaluateAnswer(UUID lessonId, LessonSubmitRequest request);
	void createLesson(LessonCreateRequest request);
	void updateLesson(UUID id, LessonUpdateRequest request);
	void deleteLesson(UUID id);
}
