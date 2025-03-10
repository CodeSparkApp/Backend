package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringLessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LessonRepository {
	private final SpringLessonRepository lessonRepository;

	public LessonRepository(@Autowired SpringLessonRepository lessonRepository) {
		this.lessonRepository = lessonRepository;
	}

	public Optional<Lesson> findById(UUID id) {
		return lessonRepository.findById(id);
	}

	public Lesson save(Lesson lesson) {
		return lessonRepository.save(lesson);
	}

	public void delete(Lesson lesson) {
		lessonRepository.delete(lesson);
	}
}
