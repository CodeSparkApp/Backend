package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.TaskCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.TaskSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TaskResponse;

import java.util.UUID;

public interface TaskService {
	TaskResponse getTaskById(UUID id);
	boolean evaluateAnswer(UUID id, TaskSubmitRequest request);
	void createTask(TaskCreateRequest request);
	void deleteTask(UUID id);
}
