package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import de.dhbw.tinf22b6.codespark.api.projection.ChapterProgressProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChapterProgressRepository extends JpaRepository<Chapter, UUID> {
	@Query(value = "SELECT chapter_id, progress FROM chapter_progress_view WHERE account_id = ?1", nativeQuery = true)
	List<ChapterProgressProjection> findProgressByAccountId(UUID accountId);
}
