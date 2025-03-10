package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ChapterRepository {
	private final SpringChapterRepository chapterRepository;

	public ChapterRepository(@Autowired SpringChapterRepository chapterRepository) {
		this.chapterRepository = chapterRepository;
	}

	public List<Chapter> findAll() {
		return chapterRepository.findAll();
	}

	public Optional<Chapter> findById(UUID id) {
		return chapterRepository.findById(id);
	}

	public Optional<Chapter> findByNextChapterId(UUID nextChapterId) {
		return chapterRepository.findByNextChapterId(nextChapterId);
	}

	public Chapter save(Chapter chapter) {
		return chapterRepository.save(chapter);
	}

	public void delete(Chapter chapter) {
		chapterRepository.delete(chapter);
	}
}
