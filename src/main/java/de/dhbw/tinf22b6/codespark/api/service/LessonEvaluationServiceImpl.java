package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.exception.UnknownLessonTypeException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.*;
import de.dhbw.tinf22b6.codespark.api.repository.UserLessonProgressRepository;
import de.dhbw.tinf22b6.codespark.api.service.dto.LessonEvaluationResult;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.PromptBuilderService;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class LessonEvaluationServiceImpl implements LessonEvaluationService {
	private final UserLessonProgressRepository userLessonProgressRepository;
	private final PromptBuilderService promptBuilderService;
	private final SimpleOpenAI simpleOpenAI;
	private final Environment env;

	public LessonEvaluationServiceImpl(@Autowired UserLessonProgressRepository userLessonProgressRepository,
									   @Autowired PromptBuilderService promptBuilderService,
									   @Autowired SimpleOpenAI simpleOpenAI,
									   @Autowired Environment env) {
		this.userLessonProgressRepository = userLessonProgressRepository;
		this.promptBuilderService = promptBuilderService;
		this.simpleOpenAI = simpleOpenAI;
		this.env = env;
	}

	@Override
	public LessonEvaluationResult evaluateLesson(Lesson lesson, LessonSubmitRequest request, Account account) {
		LessonEvaluationResult result = switch (lesson) {
			case TheoryLesson ignored -> new LessonEvaluationResult(null, true);
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
			progress.setState(result.isCorrect() ? LessonProgressState.SOLVED : LessonProgressState.ATTEMPTED);
		}

		userLessonProgressRepository.save(progress);

		return result;
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

	private LessonEvaluationResult handleCodeAnalysisLesson(CodeAnalysisLesson codeAnalysisLesson, CodeAnalysisLessonSubmitRequest request) {
		String prompt = promptBuilderService.buildPromptForCodeAnalysis(
				codeAnalysisLesson.getQuestion(),
				codeAnalysisLesson.getSampleSolution(),
				request.getSolution()
		);
		return evaluateWithAI(prompt);
	}

	private LessonEvaluationResult handleMultipleChoiceLesson(MultipleChoiceLesson multipleChoiceLesson, MultipleChoiceLessonSubmitRequest request) {
		return new LessonEvaluationResult(
				null,
				new HashSet<>(request.getSolutions()).containsAll(multipleChoiceLesson.getSolutions())
		);
	}

	private LessonEvaluationResult handleFillBlanksLesson(FillBlanksLesson fillBlanksLesson, FillBlanksLessonSubmitRequest request) {
		return new LessonEvaluationResult(
				null,
				new HashSet<>(request.getSolutions()).containsAll(fillBlanksLesson.getSolutions())
		);
	}

	private LessonEvaluationResult handleDebuggingLesson(DebuggingLesson debuggingLesson, DebuggingLessonSubmitRequest request) {
		String prompt = promptBuilderService.buildPromptForDebuggingLesson(
				debuggingLesson.getFaultyCode(),
				debuggingLesson.getExpectedOutput(),
				debuggingLesson.getSampleSolution(),
				request.getSolution()
		);
		return evaluateWithAI(prompt);
	}

	private LessonEvaluationResult handleProgrammingLesson(ProgrammingLesson programmingLesson, ProgrammingLessonSubmitRequest request) {
		String prompt = promptBuilderService.buildPromptForProgramming(
				programmingLesson.getProblem(),
				programmingLesson.getSampleSolution(),
				request.getSolution()
		);
		return evaluateWithAI(prompt);
	}

	private LessonEvaluationResult evaluateWithAI(String prompt) {
		String result = simpleOpenAI.chatCompletions()
				.create(ChatRequest.builder()
						.model(env.getRequiredProperty("openai.model.name"))
						.messages(List.of(ChatMessage.UserMessage.of(prompt)))
						.temperature(0.3)
						.build())
				.join()
				.firstContent();

		// Extract final ###true or ###false
		// TODO: Maybe try sending prompt again instead?
		String finalLine = result.strip().lines()
				.map(String::trim)
				.filter(line -> line.startsWith("###"))
				.findFirst()
				.orElse("###false");

		String explanation = result.strip().replace(finalLine, "").strip();
		boolean isCorrect = finalLine.equalsIgnoreCase("###true");

		return new LessonEvaluationResult(explanation, isCorrect);
	}
}
