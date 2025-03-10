package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.exception.ChapterNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.LessonNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.LessonRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChapterServiceImpl implements ChapterService {
	private final ChapterRepository chapterRepository;
	private final LessonRepository lessonRepository;

	public ChapterServiceImpl(@Autowired ChapterRepository chapterRepository,
							  @Autowired LessonRepository lessonRepository) {
		this.chapterRepository = chapterRepository;
		this.lessonRepository = lessonRepository;
	}

	@Override
	public ChapterOverviewResponse getChapterOverview() {
		List<Chapter> allChapters = chapterRepository.findAll();

		Set<UUID> nextChapterIds = allChapters.stream()
				.map(Chapter::getNextChapter)
				.filter(Objects::nonNull)
				.map(Chapter::getId)
				.collect(Collectors.toSet());

		Chapter firstChapter = allChapters.stream()
				.filter(c -> !nextChapterIds.contains(c.getId()))
				.findFirst()
				.orElseThrow(() -> new ChapterNotFoundException("No starting chapter found"));

		List<Chapter> sortedChapters = new ArrayList<>();
		Chapter currentChapter = firstChapter;
		while (currentChapter != null) {
			sortedChapters.add(currentChapter);
			currentChapter = currentChapter.getNextChapter();
		}

		ChapterOverviewResponse response = new ChapterOverviewResponse();
		response.setChapters(
				sortedChapters.stream()
						.map(c -> new ChapterItemResponse(c.getId(), c.getTitle()))
						.collect(Collectors.toList())
		);
		return response;
	}

	@Override
	public LessonOverviewResponse getLessonOverview(UUID chapterId) {
		Chapter chapter = chapterRepository.findById(chapterId)
						.orElseThrow(() -> new ChapterNotFoundException("No chapter was found for the provided ID"));

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
						.map(l -> new LessonItemResponse(l.getId(), l.getTitle()))
						.collect(Collectors.toList())
		);
		return response;
	}

	@Override
	public void createChapter(ChapterCreateRequest request) {
		Lesson firstLesson = lessonRepository.findById(request.getFirstLessonId())
				.orElseThrow(() -> new LessonNotFoundException("No lesson was found for the provided ID"));

		Chapter nextChapter = chapterRepository.findById(request.getNextChapterId())
				.orElseThrow(() -> new ChapterNotFoundException("No chapter was found for the provided ID"));

		Chapter chapter = new Chapter(
				request.getTitle(),
				request.getDescription(),
				firstLesson,
				nextChapter
		);

		chapterRepository.save(chapter);
	}

	@Override
	public void updateChapter(UUID id, ChapterUpdateRequest request) {

	}

	@Override
	public void deleteChapter(UUID id) {
		Chapter chapter = chapterRepository.findById(id)
				.orElseThrow(() -> new ChapterNotFoundException("No chapter was found for the provided ID"));

		chapterRepository.delete(chapter);
	}
}
