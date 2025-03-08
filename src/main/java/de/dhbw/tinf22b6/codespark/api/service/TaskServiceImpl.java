package de.dhbw.tinf22b6.codespark.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.exception.ChapterNotFoundException;
import de.dhbw.tinf22b6.codespark.api.exception.MalformedTaskException;
import de.dhbw.tinf22b6.codespark.api.exception.TaskNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.model.Task;
import de.dhbw.tinf22b6.codespark.api.payload.request.TaskCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.TaskSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TaskResponse;
import de.dhbw.tinf22b6.codespark.api.repository.ChapterRepository;
import de.dhbw.tinf22b6.codespark.api.repository.TaskRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.TaskEvaluationService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
	private final TaskRepository taskRepository;
	private final ChapterRepository chapterRepository;
	private final TaskEvaluationService taskEvaluationService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public TaskServiceImpl(@Autowired TaskRepository taskRepository,
						   @Autowired ChapterRepository chapterRepository,
						   @Autowired TaskEvaluationService taskEvaluationService) {
		this.taskRepository = taskRepository;
		this.chapterRepository = chapterRepository;
		this.taskEvaluationService = taskEvaluationService;
	}

	@Override
	public TaskResponse getTaskById(UUID id) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException("No task was found for the provided ID"));

		JsonNode contentNode;
		try {
			contentNode = objectMapper.readTree(task.getData());
		} catch (JsonProcessingException e) {
			throw new MalformedTaskException("The contents of the task couldn't be parsed");
		}

		return new TaskResponse(
				task.getId(),
				task.getType(),
				task.getTitle(),
				task.getDescription(),
				contentNode
		);
	}

	@Override
	public boolean evaluateAnswer(UUID id, TaskSubmitRequest request) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException("No task was found for the provided ID"));

		return taskEvaluationService.evaluateTask(task, request);
	}

	@Override
	public void createTask(TaskCreateRequest request) {
		Chapter chapter = chapterRepository.findById(request.getChapterId())
				.orElseThrow(() -> new ChapterNotFoundException("No chapter was found for the provided ID"));

		Task task = new Task(
				request.getType(),
				request.getTitle(),
				request.getDescription(),
				request.getContent(),
				chapter
		);

		taskRepository.save(task);
	}

	@Override
	public void deleteTask(UUID id) {
		Task task = taskRepository.findById(id)
				.orElseThrow(() -> new TaskNotFoundException("No task was found for the provided ID"));

		taskRepository.delete(task);
	}
}
