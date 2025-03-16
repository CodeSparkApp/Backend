package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonSubmitResponse;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/lesson")
public class LessonController {
	private final LessonService lessonService;

	public LessonController(@Autowired LessonService lessonService) {
		this.lessonService = lessonService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<LessonResponse> getLessonById(@PathVariable UUID id) {
		LessonResponse response = lessonService.getLessonById(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/submit")
	public ResponseEntity<LessonSubmitResponse> evaluateLesson(@PathVariable UUID id,
															   @RequestBody LessonSubmitRequest request,
															   @AuthenticationPrincipal Account account) {
		LessonSubmitResponse response = lessonService.evaluateLesson(id, request, account);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/{id}/skip")
	public ResponseEntity<LessonSubmitResponse> skipLesson(@PathVariable UUID id,
														   @AuthenticationPrincipal Account account) {
		LessonSubmitResponse response = lessonService.skipLesson(id, account);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> createLesson(@RequestBody LessonCreateRequest request) {
		lessonService.createLesson(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> updateLesson(@PathVariable UUID id, @RequestBody LessonUpdateRequest request) {
		lessonService.updateLesson(id, request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteLesson(@PathVariable UUID id) {
		lessonService.deleteLesson(id);
		return ResponseEntity.ok().build();
	}
}
