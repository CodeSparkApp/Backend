package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidLessonSubmissionException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.*;
import de.dhbw.tinf22b6.codespark.api.repository.UserLessonProgressRepository;
import de.dhbw.tinf22b6.codespark.api.service.dto.LessonEvaluationResult;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.PromptBuilderService;
import io.github.sashirestela.openai.OpenAI;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonEvaluationServiceImplTests {
	private UserLessonProgressRepository progressRepository;
	private PromptBuilderService promptBuilderService;
	private SimpleOpenAI simpleOpenAI;
	private Environment env;
	private OpenAI.ChatCompletions chatCompletions;
	private LessonEvaluationService lessonEvaluationService;

	@BeforeEach
	void setUp() {
		progressRepository = mock(UserLessonProgressRepository.class);
		promptBuilderService = mock(PromptBuilderService.class);
		simpleOpenAI = mock(SimpleOpenAI.class);
		env = mock(Environment.class);
		chatCompletions = mock(OpenAI.ChatCompletions.class);

		lessonEvaluationService = new LessonEvaluationServiceImpl(
				progressRepository, promptBuilderService, simpleOpenAI, env
		);

		when(simpleOpenAI.chatCompletions()).thenReturn(chatCompletions);
		when(env.getRequiredProperty("openai.model.name")).thenReturn("gpt-mock");
	}

	private void mockAIResponse(String content) {
		Chat mockChat = mock(Chat.class);
		when(mockChat.firstContent()).thenReturn(content);
		when(chatCompletions.create(any(ChatRequest.class))).thenReturn(CompletableFuture.completedFuture(mockChat));
	}

	@Test
	void evaluateLesson_shouldHandleTheoryLesson() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		TheoryLesson lesson = new TheoryLesson();
		lesson.setChapter(dummyChapter);
		TheoryLessonSubmitRequest request = new TheoryLessonSubmitRequest(LessonType.THEORY);

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount);

		assertTrue(result.isCorrect());
		assertNull(result.getExplanation());
		verify(progressRepository).save(any());
	}

	@Test
	void evaluateLesson_shouldHandleCodeAnalysisLesson() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		CodeAnalysisLesson lesson = new CodeAnalysisLesson(
				"title", "desc", LessonType.CODE_ANALYSIS, dummyChapter, null, null, "code", "question", "solution");
		CodeAnalysisLessonSubmitRequest request = new CodeAnalysisLessonSubmitRequest(LessonType.CODE_ANALYSIS, "mySolution");

		when(promptBuilderService.buildPromptForCodeAnalysis(any(), any(), any())).thenReturn("prompt");
		mockAIResponse("Good job\n###true");

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount);

		assertTrue(result.isCorrect());
		assertTrue(result.getExplanation().contains("Good job"));
		verify(progressRepository).save(any());
	}

	@Test
	void evaluateLesson_shouldHandleMultipleChoiceLesson() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		MultipleChoiceLesson lesson = new MultipleChoiceLesson(
				"title", "desc", LessonType.MULTIPLE_CHOICE, dummyChapter, null, null, "question", List.of("a", "b"), List.of(1));
		MultipleChoiceLessonSubmitRequest request = new MultipleChoiceLessonSubmitRequest(LessonType.MULTIPLE_CHOICE, List.of(1));

		when(promptBuilderService.buildPromptForMultipleChoiceEvaluation(any(), anyList(), anyList(), anyList(), anyBoolean()))
				.thenReturn("prompt");
		mockAIResponse("Well done!");

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount);

		assertTrue(result.isCorrect());
		assertTrue(result.getExplanation().contains("Well done"));
	}

	@Test
	void evaluateLesson_shouldHandleFillBlanksLesson() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		FillBlanksLesson lesson = new FillBlanksLesson(
				"title", "desc", LessonType.FILL_BLANKS, dummyChapter, null, null, "template", "output", List.of("a", "b"));
		FillBlanksLessonSubmitRequest request = new FillBlanksLessonSubmitRequest(LessonType.FILL_BLANKS, List.of("a", "b"));

		when(promptBuilderService.buildPromptForFillBlanksEvaluation(any(), any(), anyList(), anyList(), anyBoolean()))
				.thenReturn("prompt");
		mockAIResponse("Correct!\n###true");

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount);

		assertTrue(result.isCorrect());
		assertTrue(result.getExplanation().contains("Correct"));
	}

	@Test
	void evaluateLesson_shouldHandleDebuggingLesson() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		DebuggingLesson lesson = new DebuggingLesson(
				"title", "desc", LessonType.DEBUGGING, dummyChapter, null, null, "faulty", "expected", "solution");
		DebuggingLessonSubmitRequest request = new DebuggingLessonSubmitRequest(LessonType.DEBUGGING, "userFix");

		when(promptBuilderService.buildPromptForDebuggingLesson(any(), any(), any(), any())).thenReturn("prompt");
		mockAIResponse("Looks good\n###true");

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount);

		assertTrue(result.isCorrect());
	}

	@Test
	void evaluateLesson_shouldHandleProgrammingLesson() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		ProgrammingLesson lesson = new ProgrammingLesson(
				"title", "desc", LessonType.PROGRAMMING, dummyChapter, null, null, "problem", "code", "sample");
		ProgrammingLessonSubmitRequest request = new ProgrammingLessonSubmitRequest(LessonType.PROGRAMMING, "userCode");

		when(promptBuilderService.buildPromptForProgramming(any(), any(), any())).thenReturn("prompt");
		mockAIResponse("Perfect\n###true");

		LessonEvaluationResult result = lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount);

		assertTrue(result.isCorrect());
		assertTrue(result.getExplanation().contains("Perfect"));
	}

	@Test
	void evaluateLesson_shouldThrowOnInvalidType() {
		Account dummyAccount = mock(Account.class);

		Lesson lesson = mock(Lesson.class);
		LessonSubmitRequest request = new ProgrammingLessonSubmitRequest(LessonType.PROGRAMMING, "x");

		assertThrows(InvalidLessonSubmissionException.class,
				() -> lessonEvaluationService.evaluateLesson(lesson, request, dummyAccount));
	}

	@Test
	void skipLesson_shouldSetSkippedState() {
		Account dummyAccount = mock(Account.class);
		Chapter dummyChapter = mock(Chapter.class);

		Lesson lesson = new TheoryLesson();
		lesson.setChapter(dummyChapter);

		lessonEvaluationService.skipLesson(lesson, dummyAccount);

		verify(progressRepository).save(argThat(progress ->
				progress.getState() == LessonProgressState.SKIPPED));
	}

	@Test
	void evaluateLesson_shouldThrowInvalidLessonSubmissionException_forUnknownLessonType() {
		Account dummyAccount = mock(Account.class);

		Lesson unknownLesson = mock(Lesson.class);
		LessonSubmitRequest request = mock(LessonSubmitRequest.class);

		assertThatThrownBy(() -> lessonEvaluationService.evaluateLesson(unknownLesson, request, dummyAccount))
				.isInstanceOf(InvalidLessonSubmissionException.class)
				.hasMessageContaining("The submitted solution does not a known lesson type.");
	}

	@Test
	void evaluateLesson_shouldThrowInvalidLessonSubmissionException_onClassCastException() {
		Account dummyAccount = mock(Account.class);

		FillBlanksLesson fillBlanksLesson = mock(FillBlanksLesson.class);
		ProgrammingLessonSubmitRequest wrongRequest = mock(ProgrammingLessonSubmitRequest.class);

		assertThatThrownBy(() -> lessonEvaluationService.evaluateLesson(fillBlanksLesson, wrongRequest, dummyAccount))
				.isInstanceOf(InvalidLessonSubmissionException.class)
				.hasMessageContaining("The submitted solution does not match the expected lesson type.");
	}
}
