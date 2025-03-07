package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.TaskCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.TaskSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TaskResponse;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/task")
public class TaskController {
	private final TaskService taskService;

	public TaskController(@Autowired TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
		TaskResponse response = taskService.getTaskById(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/submit")
	public ResponseEntity<Void> submitAnswer(@PathVariable UUID id, @RequestBody TaskSubmitRequest request) {
		boolean isCorrect = taskService.evaluateAnswer(id, request);
		return isCorrect
				? ResponseEntity.ok().build()
				: ResponseEntity.badRequest().build();
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> createTask(@RequestBody TaskCreateRequest request) {
		taskService.createTask(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
		taskService.deleteTask(id);
		return ResponseEntity.ok().build();
	}
}
