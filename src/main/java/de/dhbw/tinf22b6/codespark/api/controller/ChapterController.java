package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/chapter")
public class ChapterController {
	private final ChapterService chapterService;

	public ChapterController(@Autowired ChapterService chapterService) {
		this.chapterService = chapterService;
	}

	@GetMapping("/overview")
	public ResponseEntity<ChapterOverviewResponse> getChapterOverview() {
		ChapterOverviewResponse response = chapterService.getChapterOverview();
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/{chapterId}/lessons")
	public ResponseEntity<LessonOverviewResponse> getLessonOverview(@PathVariable UUID chapterId) {
		LessonOverviewResponse response = chapterService.getLessonOverview(chapterId);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> createChapter(@RequestBody ChapterCreateRequest request) {
		chapterService.createChapter(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> updateChapter(@PathVariable UUID id, @RequestBody ChapterUpdateRequest request) {
		chapterService.updateChapter(id, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteChapter(@PathVariable UUID id) {
		chapterService.deleteChapter(id);
		return ResponseEntity.ok().build();
	}
}
