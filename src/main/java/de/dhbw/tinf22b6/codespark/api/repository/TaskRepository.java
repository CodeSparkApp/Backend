package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Task;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepository {
	private final SpringTaskRepository taskRepository;

	public TaskRepository(@Autowired SpringTaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public Optional<Task> findById(UUID id) {
		return taskRepository.findById(id);
	}

	public List<Task> findByChapterId(UUID id) {
		return taskRepository.findByChapterId(id);
	}

	public Task save(Task task) {
		return taskRepository.save(task);
	}

	public void delete(Task task) {
		taskRepository.delete(task);
	}
}
