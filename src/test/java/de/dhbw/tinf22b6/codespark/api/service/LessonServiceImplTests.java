package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonEvaluationState;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import de.dhbw.tinf22b6.codespark.api.exception.EntryNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.UnknownLessonTypeException;
import de.dhbw.tinf22b6.codespark.api.model.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.CodeAnalysisLessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.*;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.LessonRepository;
import de.dhbw.tinf22b6.codespark.api.service.dto.LessonEvaluationResult;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonEvaluationService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LessonServiceImplTests {
	private LessonRepository lessonRepository;
	private ChapterRepository chapterRepository;
	private LessonEvaluationService lessonEvaluationService;
	private LessonService lessonService;

	@BeforeEach
	void setUp() {
		this.lessonRepository = mock(LessonRepository.class);
		this.chapterRepository = mock(ChapterRepository.class);
		this.lessonEvaluationService = mock(LessonEvaluationService.class);

		this.lessonService = new LessonServiceImpl(
				lessonRepository, chapterRepository, lessonEvaluationService
		);
	}

	private final UUID lessonId = UUID.randomUUID();
	private final UUID chapterId = UUID.randomUUID();

	@Test
	void getLessonById_shouldReturnTheoryLessonResponse() {
		Chapter chapter = new Chapter();
		chapter.setId(chapterId);

		TheoryLesson lesson = new TheoryLesson();
		lesson.setId(lessonId);
		lesson.setTitle("Theory");
		lesson.setDescription("Desc");
		lesson.setType(LessonType.THEORY);
		lesson.setChapter(chapter);
		lesson.setText("Theory content");

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonResponse response = lessonService.getLessonById(lessonId);

		assertThat(response).isInstanceOf(TheoryLessonResponse.class);
		assertThat(response.getTitle()).isEqualTo("Theory");
	}

	@Test
	void evaluateLesson_shouldReturnCorrectResultWithNextLessonId() {
		Lesson lesson = mock(Lesson.class);
		Lesson nextLesson = mock(Lesson.class);
		Account account = new Account();
		LessonSubmitRequest request = mock(CodeAnalysisLessonSubmitRequest.class);

		UUID nextId = UUID.randomUUID();
		when(lesson.getNextLesson()).thenReturn(nextLesson);
		when(nextLesson.getId()).thenReturn(nextId);
		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonEvaluationResult evalResult = new LessonEvaluationResult("Well done", true);
		when(lessonEvaluationService.evaluateLesson(lesson, request, account)).thenReturn(evalResult);

		LessonSubmitResponse response = lessonService.evaluateLesson(lessonId, request, account);

		assertThat(response.getEvaluationResult()).isEqualTo(LessonEvaluationState.CORRECT);
		assertThat(response.getExplanation()).isEqualTo("Well done");
		assertThat(response.getNextLesson()).isEqualTo(nextId);
	}

	@Test
	void skipLesson_shouldReturnSkippedResultWithNextLesson() {
		Lesson lesson = mock(Lesson.class);
		Lesson nextLesson = mock(Lesson.class);
		Account account = new Account();
		UUID nextId = UUID.randomUUID();

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(lesson.getNextLesson()).thenReturn(nextLesson);
		when(nextLesson.getId()).thenReturn(nextId);

		LessonSubmitResponse response = lessonService.skipLesson(lessonId, account);

		verify(lessonEvaluationService).skipLesson(lesson, account);
		assertThat(response.getEvaluationResult()).isEqualTo(LessonEvaluationState.SKIPPED);
		assertThat(response.getNextLesson()).isEqualTo(nextId);
	}

	@Test
	void createLesson_shouldSaveTheoryLesson() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setType(LessonType.THEORY);
		request.setTitle("Title");
		request.setDescription("Desc");
		request.setText("Content");
		request.setChapterId(chapterId);

		Chapter chapter = new Chapter();
		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));

		ArgumentCaptor<Lesson> captor = ArgumentCaptor.forClass(Lesson.class);
		when(lessonRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

		lessonService.createLesson(request);

		Lesson saved = captor.getValue();
		assertThat(saved).isInstanceOf(TheoryLesson.class);
		assertThat(saved.getTitle()).isEqualTo("Title");
		verify(lessonRepository).save(saved);
	}

	@Test
	void updateLesson_shouldConnectPreviousAndNext() {
		Lesson lesson = new TheoryLesson();
		Lesson prev = new TheoryLesson();
		Lesson next = new TheoryLesson();

		lesson.setPreviousLesson(prev);
		lesson.setNextLesson(next);

		UUID prevId = UUID.randomUUID();
		UUID nextId = UUID.randomUUID();

		LessonUpdateRequest request = new LessonUpdateRequest(nextId, prevId);

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(lessonRepository.findById(prevId)).thenReturn(Optional.of(prev));
		when(lessonRepository.findById(nextId)).thenReturn(Optional.of(next));

		lessonService.updateLesson(lessonId, request);

		verify(lessonRepository, atLeastOnce()).save(any());
		assertThat(lesson.getPreviousLesson()).isEqualTo(prev);
		assertThat(lesson.getNextLesson()).isEqualTo(next);
	}

	@Test
	void deleteLesson_shouldDeleteLesson() {
		Lesson lesson = new TheoryLesson();
		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		lessonService.deleteLesson(lessonId);

		verify(lessonRepository).delete(lesson);
	}

	@Test
	void getLessonById_shouldReturnCodeAnalysisLessonResponse() {
		Chapter chapter = new Chapter();
		chapter.setId(chapterId);

		CodeAnalysisLesson lesson = new CodeAnalysisLesson();
		lesson.setId(lessonId);
		lesson.setTitle("Analysis");
		lesson.setDescription("Desc");
		lesson.setType(LessonType.CODE_ANALYSIS);
		lesson.setChapter(chapter);
		lesson.setCode("code");
		lesson.setQuestion("question");

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonResponse response = lessonService.getLessonById(lessonId);

		assertThat(response).isInstanceOf(CodeAnalysisLessonResponse.class);
		assertThat(response.getTitle()).isEqualTo("Analysis");
	}

	@Test
	void getLessonById_shouldReturnMultipleChoiceLessonResponse() {
		Chapter chapter = new Chapter();
		chapter.setId(chapterId);

		MultipleChoiceLesson lesson = new MultipleChoiceLesson();
		lesson.setId(lessonId);
		lesson.setTitle("MCQ");
		lesson.setDescription("Desc");
		lesson.setType(LessonType.MULTIPLE_CHOICE);
		lesson.setChapter(chapter);
		lesson.setQuestion("Q?");
		lesson.setOptions(List.of("A", "B", "C"));

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonResponse response = lessonService.getLessonById(lessonId);

		assertThat(response).isInstanceOf(MultipleChoiceLessonResponse.class);
		assertThat(response.getTitle()).isEqualTo("MCQ");
	}

	@Test
	void getLessonById_shouldReturnFillBlanksLessonResponse() {
		Chapter chapter = new Chapter();
		chapter.setId(chapterId);

		FillBlanksLesson lesson = new FillBlanksLesson();
		lesson.setId(lessonId);
		lesson.setTitle("FillBlanks");
		lesson.setDescription("Desc");
		lesson.setType(LessonType.FILL_BLANKS);
		lesson.setChapter(chapter);
		lesson.setTemplateCode("template");
		lesson.setExpectedOutput("output");

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonResponse response = lessonService.getLessonById(lessonId);

		assertThat(response).isInstanceOf(FillBlanksLessonResponse.class);
		assertThat(response.getTitle()).isEqualTo("FillBlanks");
	}

	@Test
	void getLessonById_shouldReturnDebuggingLessonResponse() {
		Chapter chapter = new Chapter();
		chapter.setId(chapterId);

		DebuggingLesson lesson = new DebuggingLesson();
		lesson.setId(lessonId);
		lesson.setTitle("Debug");
		lesson.setDescription("Desc");
		lesson.setType(LessonType.DEBUGGING);
		lesson.setChapter(chapter);
		lesson.setFaultyCode("int x = ;");
		lesson.setExpectedOutput("Syntax error");

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonResponse response = lessonService.getLessonById(lessonId);

		assertThat(response).isInstanceOf(DebuggingLessonResponse.class);
		assertThat(response.getTitle()).isEqualTo("Debug");
	}

	@Test
	void getLessonById_shouldReturnProgrammingLessonResponse() {
		Chapter chapter = new Chapter();
		chapter.setId(chapterId);

		ProgrammingLesson lesson = new ProgrammingLesson();
		lesson.setId(lessonId);
		lesson.setTitle("Prog");
		lesson.setDescription("Desc");
		lesson.setType(LessonType.PROGRAMMING);
		lesson.setChapter(chapter);
		lesson.setProblem("Write a loop");
		lesson.setCode("for (int i = 0; i < 5; i++) {}");

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonResponse response = lessonService.getLessonById(lessonId);

		assertThat(response).isInstanceOf(ProgrammingLessonResponse.class);
		assertThat(response.getTitle()).isEqualTo("Prog");
	}

	@Test
	void getLessonById_shouldThrowUnknownLessonTypeException() {
		Lesson unknownLesson = mock(Lesson.class);
		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(unknownLesson));

		assertThatThrownBy(() -> lessonService.getLessonById(lessonId))
				.isInstanceOf(UnknownLessonTypeException.class)
				.hasMessageContaining("The lesson type is not supported or recognized.");
	}

	@Test
	void evaluateLesson_shouldReturnIncorrectResult() {
		Lesson lesson = mock(Lesson.class);
		Account account = new Account();
		LessonSubmitRequest request = mock(LessonSubmitRequest.class);

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(lessonEvaluationService.evaluateLesson(lesson, request, account))
				.thenReturn(new LessonEvaluationResult("Try again", false));

		LessonSubmitResponse response = lessonService.evaluateLesson(lessonId, request, account);

		assertThat(response.getEvaluationResult()).isEqualTo(LessonEvaluationState.INCORRECT);
		assertThat(response.getExplanation()).isEqualTo("Try again");
		assertThat(response.getNextLesson()).isNull();
	}

	@Test
	void evaluateLesson_shouldReturnChapterCompleteSolved() {
		Lesson lesson = mock(Lesson.class);
		Account account = new Account();
		LessonSubmitRequest request = mock(LessonSubmitRequest.class);

		when(lesson.getNextLesson()).thenReturn(null);
		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(lessonEvaluationService.evaluateLesson(lesson, request, account))
				.thenReturn(new LessonEvaluationResult("Done", true));

		LessonSubmitResponse response = lessonService.evaluateLesson(lessonId, request, account);

		assertThat(response.getEvaluationResult()).isEqualTo(LessonEvaluationState.CHAPTER_COMPLETE_SOLVED);
		assertThat(response.getExplanation()).isEqualTo("Done");
		assertThat(response.getNextLesson()).isNull();
	}

	@Test
	void skipLesson_shouldReturnChapterCompleteSkipped() {
		Lesson lesson = mock(Lesson.class);
		Account account = new Account();

		when(lesson.getNextLesson()).thenReturn(null);
		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

		LessonSubmitResponse response = lessonService.skipLesson(lessonId, account);

		verify(lessonEvaluationService).skipLesson(lesson, account);
		assertThat(response.getEvaluationResult()).isEqualTo(LessonEvaluationState.CHAPTER_COMPLETE_SKIPPED);
		assertThat(response.getNextLesson()).isNull();
	}

	@Test
	void createLesson_shouldThrowWhenNextLessonNotFound() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setChapterId(chapterId);
		request.setNextLessonId(UUID.randomUUID());
		request.setType(LessonType.THEORY);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.findById(request.getNextLessonId())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> lessonService.createLesson(request))
				.isInstanceOf(EntryNotFoundException.class)
				.hasMessageContaining("next lesson");
	}

	@Test
	void createLesson_shouldThrowWhenPreviousLessonNotFound() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setChapterId(chapterId);
		request.setPreviousLessonId(UUID.randomUUID());
		request.setType(LessonType.THEORY);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.findById(request.getPreviousLessonId())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> lessonService.createLesson(request))
				.isInstanceOf(EntryNotFoundException.class)
				.hasMessageContaining("previous lesson");
	}

	@Test
	void createLesson_shouldSaveProgrammingLesson() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setType(LessonType.PROGRAMMING);
		request.setTitle("Title");
		request.setDescription("Desc");
		request.setProblem("Problem");
		request.setCode("Code");
		request.setSampleSolution("Solution");
		request.setChapterId(chapterId);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		lessonService.createLesson(request);

		verify(lessonRepository).save(argThat(lesson -> lesson instanceof ProgrammingLesson));
	}

	@Test
	void createLesson_shouldSaveMultipleChoiceLesson() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setType(LessonType.MULTIPLE_CHOICE);
		request.setTitle("MCQ");
		request.setDescription("desc");
		request.setQuestion("Q?");
		request.setOptions(List.of("A", "B", "C"));
		request.setCorrectOptions(List.of(2));
		request.setChapterId(chapterId);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		lessonService.createLesson(request);

		verify(lessonRepository).save(argThat(lesson -> lesson instanceof MultipleChoiceLesson));
	}

	@Test
	void updateLesson_shouldClearOldConnections() {
		TheoryLesson oldNext = new TheoryLesson();
		TheoryLesson oldPrev = new TheoryLesson();
		TheoryLesson lesson = new TheoryLesson();
		lesson.setNextLesson(oldNext);
		lesson.setPreviousLesson(oldPrev);

		UUID newNextId = UUID.randomUUID();
		UUID newPrevId = UUID.randomUUID();
		Lesson newNext = new TheoryLesson();
		Lesson newPrev = new TheoryLesson();

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(lessonRepository.findById(newNextId)).thenReturn(Optional.of(newNext));
		when(lessonRepository.findById(newPrevId)).thenReturn(Optional.of(newPrev));

		LessonUpdateRequest updateRequest = new LessonUpdateRequest(newNextId, newPrevId);

		lessonService.updateLesson(lessonId, updateRequest);

		verify(lessonRepository).save(oldNext);
		verify(lessonRepository).save(oldPrev);
		verify(lessonRepository).save(newNext);
		verify(lessonRepository).save(newPrev);
		verify(lessonRepository).save(lesson);

		assertThat(lesson.getNextLesson()).isEqualTo(newNext);
		assertThat(lesson.getPreviousLesson()).isEqualTo(newPrev);
	}

	@Test
	void createLesson_shouldSaveCodeAnalysisLesson() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setType(LessonType.CODE_ANALYSIS);
		request.setTitle("Analysis");
		request.setDescription("Desc");
		request.setCode("System.out.println();");
		request.setQuestion("Explain this.");
		request.setSampleSolution("It prints something.");
		request.setChapterId(chapterId);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		lessonService.createLesson(request);

		verify(lessonRepository).save(argThat(lesson -> lesson instanceof CodeAnalysisLesson));
	}

	@Test
	void createLesson_shouldSaveDebuggingLesson() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setType(LessonType.DEBUGGING);
		request.setTitle("Debug");
		request.setDescription("Fix it");
		request.setFaultyCode("buggy code");
		request.setExpectedOutput("fixed");
		request.setSampleSolution("working fix");
		request.setChapterId(chapterId);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		lessonService.createLesson(request);

		verify(lessonRepository).save(argThat(lesson -> lesson instanceof DebuggingLesson));
	}

	@Test
	void createLesson_shouldSaveFillBlanksLesson() {
		LessonCreateRequest request = new LessonCreateRequest();
		request.setType(LessonType.FILL_BLANKS);
		request.setTitle("Fill Blanks");
		request.setDescription("Complete the code");
		request.setTemplateCode("for (int i = __; i < 10; i++) { }");
		request.setExpectedOutput("0 to 9");
		request.setCorrectBlanks(List.of("0"));
		request.setChapterId(chapterId);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(new Chapter()));
		when(lessonRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		lessonService.createLesson(request);

		verify(lessonRepository).save(argThat(lesson -> lesson instanceof FillBlanksLesson));
	}
}
