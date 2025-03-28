package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.exception.UnknownLessonTypeException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.*;
import de.dhbw.tinf22b6.codespark.api.repository.UserLessonProgressRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import io.github.sashirestela.openai.SimpleOpenAI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;

@Service
public class LessonEvaluationServiceImpl implements LessonEvaluationService {
	private final UserLessonProgressRepository userLessonProgressRepository;
	private final SimpleOpenAI simpleOpenAI;

	public LessonEvaluationServiceImpl(@Autowired UserLessonProgressRepository userLessonProgressRepository,
									   @Autowired SimpleOpenAI simpleOpenAI) {
		this.userLessonProgressRepository = userLessonProgressRepository;
		this.simpleOpenAI = simpleOpenAI;
	}

	@Override
	public boolean evaluateLesson(Lesson lesson, LessonSubmitRequest request, Account account) {
		boolean isCorrect = switch (lesson) {
			case TheoryLesson ignored -> true;
			case CodeAnalysisLesson codeAnalysisLesson -> handleCodeAnalysisLesson(codeAnalysisLesson, (CodeAnalysisLessonSubmitRequest) request);
			case MultipleChoiceLesson multipleChoiceLesson -> handleMultipleChoiceLesson(multipleChoiceLesson, (MultipleChoiceLessonSubmitRequest) request);
			case FillBlanksLesson fillBlanksLesson -> handleFillBlanksLesson(fillBlanksLesson, (FillBlanksLessonSubmitRequest) request);
			case DebuggingLesson debuggingLesson -> handleDebuggingLesson(debuggingLesson, (DebuggingLessonSubmitRequest) request);
			case ProgrammingLesson programmingLesson -> handleProgrammingLesson(programmingLesson, (ProgrammingLessonSubmitRequest) request);
			default -> throw new UnknownLessonTypeException("Unexpected lesson type: " + lesson.getClass().getSimpleName());
		};
		// TODO: Handle casting exception

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

	private boolean handleCodeAnalysisLesson(CodeAnalysisLesson codeAnalysisLesson, CodeAnalysisLessonSubmitRequest request) {
		return Objects.equals(request.getSolution(), codeAnalysisLesson.getSampleSolution());
	}

	private boolean handleMultipleChoiceLesson(MultipleChoiceLesson multipleChoiceLesson, MultipleChoiceLessonSubmitRequest request) {
		return new HashSet<>(request.getSolutions()).containsAll(multipleChoiceLesson.getSolutions());
	}

	private boolean handleFillBlanksLesson(FillBlanksLesson fillBlanksLesson, FillBlanksLessonSubmitRequest request) {
		return new HashSet<>(request.getSolutions()).containsAll(fillBlanksLesson.getSolutions());
	}

	private boolean handleDebuggingLesson(DebuggingLesson debuggingLesson, DebuggingLessonSubmitRequest request) {
		return Objects.equals(request.getSolution(), debuggingLesson.getSampleSolution());
	}

	private boolean handleProgrammingLesson(ProgrammingLesson programmingLesson, ProgrammingLessonSubmitRequest request) {
		return Objects.equals(request.getSolution(), programmingLesson.getSampleSolution());
	}
}
