package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonOverviewResponse;

import java.util.UUID;

public interface ChapterService {
	ChapterOverviewResponse getChapterOverview(Account account);
	LessonOverviewResponse getLessonOverview(UUID chapterId, Account account);
	void createChapter(ChapterCreateRequest request);
	void updateChapter(UUID id, ChapterUpdateRequest request);
	void deleteChapter(UUID id);
}
