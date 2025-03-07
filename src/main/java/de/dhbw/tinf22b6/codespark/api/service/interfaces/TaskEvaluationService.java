package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Task;
import de.dhbw.tinf22b6.codespark.api.payload.request.TaskSubmitRequest;

public interface TaskEvaluationService {
	boolean evaluateTask(Task task, TaskSubmitRequest request);
}
