package de.dhbw.tinf22b6.codespark.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.model.Task;
import de.dhbw.tinf22b6.codespark.api.payload.request.TaskSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.TaskEvaluationService;
import org.springframework.stereotype.Service;

@Service
public class TaskEvaluationServiceImpl implements TaskEvaluationService {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean evaluateTask(Task task, TaskSubmitRequest request) {
		return true;
	}
}
