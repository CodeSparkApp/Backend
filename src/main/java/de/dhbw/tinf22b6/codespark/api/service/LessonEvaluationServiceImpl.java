package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.exception.UnknownLessonTypeException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.repository.UserLessonProgressRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonEvaluationServiceImpl implements LessonEvaluationService {
	private final UserLessonProgressRepository userLessonProgressRepository;

	public LessonEvaluationServiceImpl(@Autowired UserLessonProgressRepository userLessonProgressRepository) {
		this.userLessonProgressRepository = userLessonProgressRepository;
	}

	@Override
	public boolean evaluateLesson(Lesson lesson, LessonSubmitRequest request, Account account) {
		boolean isCorrect = switch (lesson) {
			case TheoryLesson ignored -> true;
			case CodeAnalysisLesson codeAnalysisLesson -> handleCodeAnalysisLesson(codeAnalysisLesson, request);
			case MultipleChoiceLesson multipleChoiceLesson -> handleMultipleChoiceLesson(multipleChoiceLesson, request);
			case FillBlanksLesson fillBlanksLesson -> handleFillBlanksLesson(fillBlanksLesson, request);
			case DebuggingLesson debuggingLesson -> handleDebuggingLesson(debuggingLesson, request);
			case ProgrammingLesson programmingLesson -> handleProgrammingLesson(programmingLesson, request);
			default -> throw new UnknownLessonTypeException("Unexpected lesson type: " + lesson.getClass().getSimpleName());
		};

		UserLessonProgress progress = userLessonProgressRepository.findByAccountAndLesson(account, lesson)
				.orElse(new UserLessonProgress(account, lesson, LessonProgressState.UNATTEMPTED));

		if (progress.getState() != LessonProgressState.SOLVED) {
			progress.setState(isCorrect ? LessonProgressState.SOLVED : LessonProgressState.ATTEMPTED);
		}

		userLessonProgressRepository.save(progress);

		return isCorrect;
	}

	@Override
	public void skipLesson(Lesson lesson, Account account) {
		UserLessonProgress progress = userLessonProgressRepository.findByAccountAndLesson(account, lesson)
				.orElse(new UserLessonProgress(account, lesson, LessonProgressState.UNATTEMPTED));

		if (progress.getState() != LessonProgressState.SOLVED) {
			progress.setState(LessonProgressState.SKIPPED);
		}

		userLessonProgressRepository.save(progress);
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
