package de.dhbw.tinf22b6.codespark.api.repository.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringChapterRepository extends JpaRepository<Chapter, UUID> {
	@Query("SELECT c FROM Chapter c WHERE c.nextChapter.id = :nextChapterId")
	Optional<Chapter> findByNextChapterId(@Param("nextChapterId") UUID nextChapterId);
}
