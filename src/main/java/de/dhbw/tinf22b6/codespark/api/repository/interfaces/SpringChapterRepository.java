package de.dhbw.tinf22b6.codespark.api.repository.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringChapterRepository extends JpaRepository<Chapter, UUID> {}
