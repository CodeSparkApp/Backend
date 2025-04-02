package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonEvaluationState;
import de.dhbw.tinf22b6.codespark.api.exception.ChapterNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.LessonNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.UnknownLessonTypeException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.*;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.LessonRepository;
import de.dhbw.tinf22b6.codespark.api.service.dto.LessonEvaluationResult;
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

	public LessonServiceImpl(@Autowired LessonRepository lessonRepository,
							 @Autowired ChapterRepository chapterRepository,
							 @Autowired LessonEvaluationService lessonEvaluationService) {
		this.lessonRepository = lessonRepository;
		this.chapterRepository = chapterRepository;
		this.lessonEvaluationService = lessonEvaluationService;
	}

	@Override
	@Transactional
	public LessonResponse getLessonById(UUID id) {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		return switch (lesson) {
			case TheoryLesson theoryLesson -> new TheoryLessonResponse(
					theoryLesson.getId(),
					theoryLesson.getTitle(),
					theoryLesson.getDescription(),
					theoryLesson.getType(),
					theoryLesson.getChapter().getId(),
					theoryLesson.getText()
			);
			case CodeAnalysisLesson codeAnalysisLesson -> new CodeAnalysisLessonResponse(
					codeAnalysisLesson.getId(),
					codeAnalysisLesson.getTitle(),
					codeAnalysisLesson.getDescription(),
					codeAnalysisLesson.getType(),
					codeAnalysisLesson.getChapter().getId(),
					codeAnalysisLesson.getCode(),
					codeAnalysisLesson.getQuestion()
			);
			case MultipleChoiceLesson multipleChoiceLesson -> new MultipleChoiceLessonResponse(
					multipleChoiceLesson.getId(),
					multipleChoiceLesson.getTitle(),
					multipleChoiceLesson.getDescription(),
					multipleChoiceLesson.getType(),
					multipleChoiceLesson.getChapter().getId(),
					multipleChoiceLesson.getQuestion(),
					multipleChoiceLesson.getOptions()
			);
			case FillBlanksLesson fillBlanksLesson -> new FillBlanksLessonResponse(
					fillBlanksLesson.getId(),
					fillBlanksLesson.getTitle(),
					fillBlanksLesson.getDescription(),
					fillBlanksLesson.getType(),
					fillBlanksLesson.getChapter().getId(),
					fillBlanksLesson.getTemplateCode(),
					fillBlanksLesson.getExpectedOutput()
			);
			case DebuggingLesson debuggingLesson -> new DebuggingLessonResponse(
					debuggingLesson.getId(),
					debuggingLesson.getTitle(),
					debuggingLesson.getDescription(),
					debuggingLesson.getType(),
					debuggingLesson.getChapter().getId(),
					debuggingLesson.getFaultyCode(),
					debuggingLesson.getExpectedOutput()
			);
			case ProgrammingLesson programmingLesson -> new ProgrammingLessonResponse(
					programmingLesson.getId(),
					programmingLesson.getTitle(),
					programmingLesson.getDescription(),
					programmingLesson.getType(),
					programmingLesson.getChapter().getId(),
					programmingLesson.getProblem(),
					programmingLesson.getCode()
			);
			default -> throw new UnknownLessonTypeException("Unexpected lesson type: " + lesson.getClass().getSimpleName());
		};
	}

	@Override
	public LessonSubmitResponse evaluateLesson(UUID id, LessonSubmitRequest request, Account account) {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, account);
		if (!result.isCorrect()) {
			return new LessonSubmitResponse(LessonEvaluationState.INCORRECT, result.getExplanation(), null);
		}

		Lesson nextLesson = lesson.getNextLesson();
		if (nextLesson != null) {
			return new LessonSubmitResponse(LessonEvaluationState.CORRECT, result.getExplanation(), nextLesson.getId());
		}

		return new LessonSubmitResponse(LessonEvaluationState.CHAPTER_COMPLETE_SOLVED, result.getExplanation(), null);
	}

	@Override
	public LessonSubmitResponse skipLesson(UUID id, Account account) {
		Lesson lesson = lessonRepository.findById(id)
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		lessonEvaluationService.skipLesson(lesson, account);

		Lesson nextLesson = lesson.getNextLesson();
		if (nextLesson != null) {
			return new LessonSubmitResponse(LessonEvaluationState.SKIPPED, null, nextLesson.getId());
		}

		return new LessonSubmitResponse(LessonEvaluationState.CHAPTER_COMPLETE_SKIPPED, null, null);
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

		Lesson newLesson;

		switch (request.getType()) {
			case THEORY -> newLesson = new TheoryLesson(
					request.getTitle(),
					request.getDescription(),
					request.getType(),
					chapter,
					nextLesson,
					previousLesson,
					request.getText()
			);
			case PROGRAMMING -> newLesson = new ProgrammingLesson(
					request.getTitle(),
					request.getDescription(),
					request.getType(),
					chapter,
					nextLesson,
					previousLesson,
					request.getProblem(),
					request.getCode(),
					request.getSampleSolution()
			);
			case CODE_ANALYSIS -> newLesson = new CodeAnalysisLesson(
					request.getTitle(),
					request.getDescription(),
					request.getType(),
					chapter,
					nextLesson,
					previousLesson,
					request.getCode(),
					request.getQuestion(),
					request.getSampleSolution()
			);
			case DEBUGGING -> newLesson = new DebuggingLesson(
					request.getTitle(),
					request.getDescription(),
					request.getType(),
					chapter,
					nextLesson,
					previousLesson,
					request.getFaultyCode(),
					request.getExpectedOutput(),
					request.getSampleSolution()
			);
			case FILL_BLANKS -> newLesson = new FillBlanksLesson(
					request.getTitle(),
					request.getDescription(),
					request.getType(),
					chapter,
					nextLesson,
					previousLesson,
					request.getTemplateCode(),
					request.getExpectedOutput(),
					request.getCorrectBlanks()
			);
			case MULTIPLE_CHOICE -> newLesson = new MultipleChoiceLesson(
					request.getTitle(),
					request.getDescription(),
					request.getType(),
					chapter,
					nextLesson,
					previousLesson,
					request.getQuestion(),
					request.getOptions(),
					request.getCorrectOptions()
			);
			default -> throw new IllegalArgumentException("Invalid lesson type provided: " + request.getType());
		}

		Lesson savedLesson = lessonRepository.save(newLesson);

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
