package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.exception.EntryNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.model.UserLessonProgress;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.projection.ChapterProgressProjection;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterProgressRepository;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.LessonRepository;
import de.dhbw.tinf22b6.codespark.api.repository.UserLessonProgressRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ChapterService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChapterServiceImpl implements ChapterService {
	private final ChapterRepository chapterRepository;
	private final LessonRepository lessonRepository;
	private final ChapterProgressRepository chapterProgressRepository;
	private final UserLessonProgressRepository userLessonProgressRepository;

	public ChapterServiceImpl(@Autowired ChapterRepository chapterRepository,
							  @Autowired LessonRepository lessonRepository,
							  @Autowired ChapterProgressRepository chapterProgressRepository,
							  @Autowired UserLessonProgressRepository userLessonProgressRepository) {
		this.chapterRepository = chapterRepository;
		this.lessonRepository = lessonRepository;
		this.chapterProgressRepository = chapterProgressRepository;
		this.userLessonProgressRepository = userLessonProgressRepository;
	}

	@Override
	public ChapterOverviewResponse getChapterOverview(Account account) {
		List<Chapter> allChapters = chapterRepository.findAll();

		// TODO: Use views for the chapter sorting logic?
		Set<UUID> nextChapterIds = allChapters.stream()
				.map(Chapter::getNextChapter)
				.filter(Objects::nonNull)
				.map(Chapter::getId)
				.collect(Collectors.toSet());

		Chapter firstChapter = allChapters.stream()
				.filter(c -> !nextChapterIds.contains(c.getId()))
				.findFirst()
				.orElseThrow(() -> new EntryNotFoundException("The specified first chapter could not be found."));

		List<Chapter> sortedChapters = new ArrayList<>();
		Chapter currentChapter = firstChapter;
		while (currentChapter != null) {
			sortedChapters.add(currentChapter);
			currentChapter = currentChapter.getNextChapter();
		}

		List<ChapterProgressProjection> chapterProgress = chapterProgressRepository.findProgressByAccountId(account.getId());
		Map<UUID, Float> progressMap = chapterProgress.stream() // Convert list to map for fast lookups
				.collect(Collectors.toMap(ChapterProgressProjection::getChapterId, ChapterProgressProjection::getProgress));

		ChapterOverviewResponse response = new ChapterOverviewResponse();
		response.setChapters(
				sortedChapters.stream()
						.map(c -> new ChapterItemResponse(
								c.getId(),
								c.getTitle(),
								progressMap.getOrDefault(c.getId(), 0.f) // Default to 0 if no progress entry
						))
						.collect(Collectors.toList())
		);
		return response;
	}

	@Override
	@Transactional
	public LessonOverviewResponse getLessonOverview(UUID chapterId, Account account) {
		Chapter chapter = chapterRepository.findById(chapterId)
						.orElseThrow(() -> new EntryNotFoundException("The requested chapter could not be found."));

		// TODO: Use views for the lesson sorting logic?
		Lesson firstLesson = chapter.getFirstLesson();
		List<Lesson> lessons = new ArrayList<>();

		Lesson currentLesson = firstLesson;
		while (currentLesson != null) {
			lessons.add(currentLesson);
			currentLesson = currentLesson.getNextLesson();
		}

		LessonOverviewResponse response = new LessonOverviewResponse();
		response.setChapterTitle(chapter.getTitle());
		response.setChapterDescription(chapter.getDescription());
		response.setLessons(
				lessons.stream()
						.map(l -> {
								// Fetch progress or create a new entry
								LessonProgressState state = userLessonProgressRepository.findByAccountAndLesson(account, l)
								.map(UserLessonProgress::getState)
								.orElseGet(() -> {
									// Create and save a new progress entry
									// TODO: Since the result is always 'LessonProgressState.UNATTEMPTED',
									// 		 this logic can be executed in a different thread
									UserLessonProgress newProgress = new UserLessonProgress(account, l, LessonProgressState.UNATTEMPTED);
									userLessonProgressRepository.save(newProgress);
									return newProgress.getState();
								});

								return new LessonItemResponse(l.getId(), l.getTitle(), state);
						})
						.collect(Collectors.toList())
		);
		return response;
	}

	@Override
	@Transactional
	public void createChapter(ChapterCreateRequest request) {
		Lesson firstLesson = (request.getFirstLessonId() != null)
				? lessonRepository.findById(request.getFirstLessonId())
				.orElseThrow(() -> new EntryNotFoundException("The specified first lesson could not be found."))
				: null;

		Chapter nextChapter = (request.getNextChapterId() != null)
				? chapterRepository.findById(request.getNextChapterId())
				.orElseThrow(() -> new EntryNotFoundException("The specified next chapter could not be found."))
				: null;

		Chapter chapter = new Chapter(
				request.getTitle(),
				request.getDescription(),
				firstLesson,
				nextChapter
		);

		Chapter savedChapter = chapterRepository.save(chapter);

		updateNextChapterReference(nextChapter, savedChapter);
	}

	@Override
	@Transactional
	public void updateChapter(UUID id, ChapterUpdateRequest request) {
		Chapter chapter = chapterRepository.findById(id)
				.orElseThrow(() -> new EntryNotFoundException("The requested chapter could not be found."));

		// Retrieve the new nextChapter (can be null)
		Chapter nextChapter = (request.getNextChapterId() != null)
				? chapterRepository.findById(request.getNextChapterId()).orElse(null)
				: null;

		// Disconnect the old nextChapter if needed
		if (chapter.getNextChapter() != null && !chapter.getNextChapter().equals(nextChapter)) {
			Chapter oldNextChapter = chapter.getNextChapter();
			oldNextChapter.setNextChapter(null);
			chapterRepository.save(oldNextChapter);
		}

		chapter.setNextChapter(nextChapter);

		updateNextChapterReference(nextChapter, chapter);

		chapterRepository.save(chapter);
	}

	@Override
	public void deleteChapter(UUID id) {
		Chapter chapter = chapterRepository.findById(id)
				.orElseThrow(() -> new EntryNotFoundException("The requested chapter could not be found."));

		chapterRepository.delete(chapter);
	}

	private void updateNextChapterReference(Chapter nextChapter, Chapter newChapter) {
		if (nextChapter != null) {
			chapterRepository.findByNextChapterId(nextChapter.getId()).ifPresent(previousChapter -> {
				previousChapter.setNextChapter(newChapter);
				chapterRepository.save(previousChapter);
			});
		}
	}
}
