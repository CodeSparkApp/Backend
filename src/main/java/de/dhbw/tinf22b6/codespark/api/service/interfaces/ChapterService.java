package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.StoryChaptersResponse;

import java.util.UUID;

public interface ChapterService {
	StoryChaptersResponse getAllStoryChapters();
	void createChapter(ChapterCreateRequest request);
	void deleteChapter(UUID id);
}
