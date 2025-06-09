package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.exception.EntryNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterProgressRepository;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.LessonRepository;
import de.dhbw.tinf22b6.codespark.api.repository.UserLessonProgressRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ChapterServiceImplTests {
	private ChapterRepository chapterRepository;
	private LessonRepository lessonRepository;
	private ChapterProgressRepository chapterProgressRepository;
	private UserLessonProgressRepository userLessonProgressRepository;
	private ChapterService chapterService;

	@BeforeEach
	void setUp() {
		chapterRepository = mock(ChapterRepository.class);
		lessonRepository = mock(LessonRepository.class);
		chapterProgressRepository = mock(ChapterProgressRepository.class);
		userLessonProgressRepository = mock(UserLessonProgressRepository.class);

		chapterService = new ChapterServiceImpl(
				chapterRepository, lessonRepository, chapterProgressRepository, userLessonProgressRepository
		);
	}

	@Test
	void getChapterOverview_shouldReturnOrderedChaptersWithProgress() {
		Chapter c1 = new Chapter("Chapter 1", "Desc", null, null);
		Chapter c2 = new Chapter("Chapter 2", "Desc", null, null);
		c1.setId(UUID.randomUUID());
		c2.setId(UUID.randomUUID());
		c1.setNextChapter(c2);

		when(chapterRepository.findAll()).thenReturn(List.of(c1, c2));
		when(chapterProgressRepository.findProgressByAccountId(any())).thenReturn(List.of());

		Account account = new Account();
		ChapterOverviewResponse response = chapterService.getChapterOverview(account);

		assertThat(response.getChapters()).hasSize(2);
		assertThat(response.getChapters().get(0).getTitle()).isEqualTo("Chapter 1");
	}

	@Test
	void getChapterOverview_shouldThrowIfFirstChapterNotFound() {
		Chapter orphan = new Chapter("Orphan", "Desc", null, null);
		orphan.setId(UUID.randomUUID());
		orphan.setNextChapter(null);
		when(chapterRepository.findAll()).thenReturn(List.of(orphan));
		when(chapterProgressRepository.findProgressByAccountId(any())).thenReturn(List.of());

		orphan.setNextChapter(new Chapter());
		orphan.getNextChapter().setId(orphan.getId()); // Self-loop

		assertThatThrownBy(() -> chapterService.getChapterOverview(new Account()))
				.isInstanceOf(EntryNotFoundException.class);
	}

	@Test
	void getLessonOverview_shouldReturnLessonsWithProgress() {
		UUID chapterId = UUID.randomUUID();
		Chapter chapter = new Chapter("Chapter 1", "Desc", null, null);
		chapter.setId(chapterId);

		Lesson l1 = mock(Lesson.class);
		Lesson l2 = mock(Lesson.class);
		when(l1.getNextLesson()).thenReturn(l2);
		when(l2.getNextLesson()).thenReturn(null);
		when(l1.getTitle()).thenReturn("Lesson 1");
		when(l2.getTitle()).thenReturn("Lesson 2");
		when(l1.getId()).thenReturn(UUID.randomUUID());
		when(l2.getId()).thenReturn(UUID.randomUUID());

		chapter.setFirstLesson(l1);

		when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
		when(userLessonProgressRepository.findByAccountAndLesson(any(), any()))
				.thenReturn(Optional.empty());

		Account account = new Account();
		LessonOverviewResponse response = chapterService.getLessonOverview(chapterId, account);

		assertThat(response.getLessons()).hasSize(2);
		verify(userLessonProgressRepository, times(2)).save(any());
	}

	@Test
	void getLessonOverview_shouldThrowIfChapterMissing() {
		when(chapterRepository.findById(any())).thenReturn(Optional.empty());
		assertThatThrownBy(() -> chapterService.getLessonOverview(UUID.randomUUID(), new Account()))
				.isInstanceOf(EntryNotFoundException.class);
	}

	@Test
	void createChapter_shouldCreateChapterWithLessonAndNext() {
		UUID lessonId = UUID.randomUUID();
		UUID nextChapterId = UUID.randomUUID();
		Lesson lesson = new Lesson() {};
		lesson.setId(lessonId);
		Chapter nextChapter = new Chapter("Next", "Desc", null, null);
		nextChapter.setId(nextChapterId);

		when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
		when(chapterRepository.findById(nextChapterId)).thenReturn(Optional.of(nextChapter));
		when(chapterRepository.save(any())).thenAnswer(i -> i.getArgument(0));

		ChapterCreateRequest request = new ChapterCreateRequest("Chapter 1", "Desc", lessonId, nextChapterId);
		chapterService.createChapter(request);

		verify(chapterRepository, times(1)).save(any());
	}

	@Test
	void updateChapter_shouldReplaceNextChapterAndDisconnectOld() {
		Chapter oldNext = new Chapter("Old", "Desc", null, null);
		Chapter chapter = new Chapter("Main", "Desc", null, oldNext);
		chapter.setId(UUID.randomUUID());
		oldNext.setId(UUID.randomUUID());

		Chapter newNext = new Chapter("New", "Desc", null, null);
		newNext.setId(UUID.randomUUID());

		when(chapterRepository.findById(chapter.getId())).thenReturn(Optional.of(chapter));
		when(chapterRepository.findById(newNext.getId())).thenReturn(Optional.of(newNext));
		when(chapterRepository.findByNextChapterId(newNext.getId())).thenReturn(Optional.empty());

		ChapterUpdateRequest req = new ChapterUpdateRequest(null, newNext.getId());
		chapterService.updateChapter(chapter.getId(), req);

		verify(chapterRepository, times(2)).save(any());
	}

	@Test
	void createChapter_shouldUpdatePreviousChapterReference() {
		UUID nextChapterId = UUID.randomUUID();
		UUID newChapterId = UUID.randomUUID();
		UUID previousChapterId = UUID.randomUUID();

		Chapter nextChapter = new Chapter();
		nextChapter.setId(nextChapterId);

		Chapter newChapter = new Chapter();
		newChapter.setId(newChapterId);

		Chapter previousChapter = new Chapter();
		previousChapter.setId(previousChapterId);

		ChapterCreateRequest request = new ChapterCreateRequest();
		request.setTitle("Chapter 2");
		request.setDescription("Description");
		request.setNextChapterId(nextChapterId);

		when(chapterRepository.findById(nextChapterId)).thenReturn(Optional.of(nextChapter));

		when(chapterRepository.save(any(Chapter.class))).thenAnswer(invocation -> {
			Chapter chapter = invocation.getArgument(0);
			chapter.setId(newChapterId);
			return chapter;
		});

		when(chapterRepository.findByNextChapterId(nextChapterId)).thenReturn(Optional.of(previousChapter));

		chapterService.createChapter(request);

		assertThat(previousChapter.getNextChapter().getId()).isEqualTo(newChapterId);
		verify(chapterRepository).save(previousChapter);
	}

	@Test
	void deleteChapter_shouldDeleteIfExists() {
		Chapter chapter = new Chapter("ToDelete", "Desc", null, null);
		chapter.setId(UUID.randomUUID());
		when(chapterRepository.findById(chapter.getId())).thenReturn(Optional.of(chapter));

		chapterService.deleteChapter(chapter.getId());

		verify(chapterRepository).delete(chapter);
	}
}
