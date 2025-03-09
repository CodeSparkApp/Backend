package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.exception.ChapterNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.StoryChaptersResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChapterServiceImpl implements ChapterService {
	private final ChapterRepository chapterRepository;

	public ChapterServiceImpl(@Autowired ChapterRepository chapterRepository) {
		this.chapterRepository = chapterRepository;
	}

	@Override
	public StoryChaptersResponse getAllStoryChapters() {
		StoryChaptersResponse response = new StoryChaptersResponse();
		response.setChapters(chapterRepository.findAll());
		return response;
	}

	@Override
	public void createChapter(ChapterCreateRequest request) {
		Chapter chapter = new Chapter(
				request.getTitle(),
				request.getDescription()
		);

		chapterRepository.save(chapter);
	}

	@Override
	public void deleteChapter(UUID id) {
		Chapter chapter = chapterRepository.findById(id)
				.orElseThrow(() -> new ChapterNotFoundException("No chapter was found for the provided ID"));

		chapterRepository.delete(chapter);
	}
}
