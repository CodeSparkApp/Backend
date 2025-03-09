package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.StoryChaptersResponse;
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

	@GetMapping("/story")
	public ResponseEntity<StoryChaptersResponse> getAllStoryChapters() {
		StoryChaptersResponse response = chapterService.getAllStoryChapters();
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> createChapter(@RequestBody ChapterCreateRequest request) {
		chapterService.createChapter(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteChapter(@PathVariable UUID id) {
		chapterService.deleteChapter(id);
		return ResponseEntity.ok().build();
	}
}
