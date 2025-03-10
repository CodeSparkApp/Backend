package de.dhbw.tinf22b6.codespark.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.exception.ChapterNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.LessonNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.MalformedLessonDataException;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonSubmitResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.LessonRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LessonServiceImpl implements LessonService {
	private final LessonRepository lessonRepository;
	private final ChapterRepository chapterRepository;
	private final LessonEvaluationService lessonEvaluationService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public LessonServiceImpl(@Autowired LessonRepository lessonRepository,
							 @Autowired ChapterRepository chapterRepository,
							 @Autowired LessonEvaluationService lessonEvaluationService) {
		this.lessonRepository = lessonRepository;
		this.chapterRepository = chapterRepository;
		this.lessonEvaluationService = lessonEvaluationService;
	}

	@Override
	public LessonResponse getLessonById(UUID id) {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		JsonNode contentNode;
		try {
			contentNode = objectMapper.readTree(lesson.getData());
		} catch (JsonProcessingException e) {
			throw new MalformedLessonDataException("The data of the lesson couldn't be parsed");
		}

		return new LessonResponse(
				lesson.getId(),
				lesson.getTitle(),
				lesson.getDescription(),
				lesson.getType(),
				contentNode
		);
	}

	@Override
	public LessonSubmitResponse evaluateAnswer(UUID lessonId, LessonSubmitRequest request) {
		Lesson lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		return lessonEvaluationService.evaluateLesson(lesson, request)
				? new LessonSubmitResponse(lesson.getNextLesson() != null ? lesson.getNextLesson().getId() : null)
				: null;
	}

	@Override
	@Transactional
	public void createLesson(LessonCreateRequest request) {
		Chapter chapter = chapterRepository.findById(request.getChapterId())
				.orElseThrow(() -> new ChapterNotFoundException("No chapter was found for the provided ID"));

		Lesson nextLesson = (request.getNextLessonId() != null)
				? lessonRepository.findById(request.getNextLessonId())
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID of the 'next lesson'"))
				: null;

		Lesson previousLesson = (request.getPreviousLessonId() != null)
				? lessonRepository.findById(request.getPreviousLessonId())
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID of the 'previous lesson'"))
				: null;

		Lesson lesson = new Lesson(
				request.getTitle(),
				request.getDescription(),
				request.getType(),
				request.getData(),
				chapter,
				nextLesson,
				previousLesson
		);

		Lesson savedLesson = lessonRepository.save(lesson);

		updateAdjacentLessons(previousLesson, nextLesson, savedLesson);
	}

	@Override
	@Transactional
	public void updateLesson(UUID id, LessonUpdateRequest request) {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("Lesson not found for provided ID"));

		Lesson nextLesson = (request.getNextLessonId() != null)
				? lessonRepository.findById(request.getNextLessonId()).orElse(null)
				: null;

		Lesson previousLesson = (request.getPreviousLessonId() != null)
				? lessonRepository.findById(request.getPreviousLessonId()).orElse(null)
				: null;

		// Disconnect the old nextLesson reference if needed
		if (lesson.getNextLesson() != null && !lesson.getNextLesson().equals(nextLesson)) {
			lesson.getNextLesson().setPreviousLesson(null);
			lessonRepository.save(lesson.getNextLesson());
		}

		// Disconnect the old previousLesson reference if needed
		if (lesson.getPreviousLesson() != null && !lesson.getPreviousLesson().equals(previousLesson)) {
			lesson.getPreviousLesson().setNextLesson(null);
			lessonRepository.save(lesson.getPreviousLesson());
		}

		lesson.setNextLesson(nextLesson);
		lesson.setPreviousLesson(previousLesson);

		updateAdjacentLessons(previousLesson, nextLesson, lesson);

		lessonRepository.save(lesson);
	}

	@Override
	public void deleteLesson(UUID id) {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		lessonRepository.delete(lesson);
	}

	private void updateAdjacentLessons(Lesson previousLesson, Lesson nextLesson, Lesson currentLesson) {
		if (previousLesson != null) {
			previousLesson.setNextLesson(currentLesson);
			lessonRepository.save(previousLesson);
		}

		if (nextLesson != null) {
			nextLesson.setPreviousLesson(currentLesson);
			lessonRepository.save(nextLesson);
		}
	}
}
