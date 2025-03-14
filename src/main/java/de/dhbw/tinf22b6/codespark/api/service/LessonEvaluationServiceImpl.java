package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.exception.UnknownLessonTypeException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import org.springframework.stereotype.Service;

@Service
public class LessonEvaluationServiceImpl implements LessonEvaluationService {
	@Override
	public boolean evaluateLesson(Lesson lesson, LessonSubmitRequest request) {
		return switch (lesson) {
			case TheoryLesson ignored -> true;
			case CodeAnalysisLesson codeAnalysisLesson -> handleCodeAnalysisLesson(codeAnalysisLesson, request);
			case MultipleChoiceLesson multipleChoiceLesson -> handleMultipleChoiceLesson(multipleChoiceLesson, request);
			case FillBlanksLesson fillBlanksLesson -> handleFillBlanksLesson(fillBlanksLesson, request);
			case DebuggingLesson debuggingLesson -> handleDebuggingLesson(debuggingLesson, request);
			case ProgrammingLesson programmingLesson -> handleProgrammingLesson(programmingLesson, request);
			default -> throw new UnknownLessonTypeException("Unexpected lesson type: " + lesson.getClass().getSimpleName());
		};
	}

	private boolean handleCodeAnalysisLesson(CodeAnalysisLesson codeAnalysisLesson, LessonSubmitRequest request) {
		return true;
	}

	private boolean handleMultipleChoiceLesson(MultipleChoiceLesson multipleChoiceLesson, LessonSubmitRequest request) {
		return true;
	}

	private boolean handleFillBlanksLesson(FillBlanksLesson fillBlanksLesson, LessonSubmitRequest request) {
		return true;
	}

	private boolean handleDebuggingLesson(DebuggingLesson debuggingLesson, LessonSubmitRequest request) {
		return true;
	}

	private boolean handleProgrammingLesson(ProgrammingLesson programmingLesson, LessonSubmitRequest request) {
		return true;
	}
}
