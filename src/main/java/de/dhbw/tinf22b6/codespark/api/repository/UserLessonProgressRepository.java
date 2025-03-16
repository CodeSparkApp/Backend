package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Lesson;
import de.dhbw.tinf22b6.codespark.api.model.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, UUID> {
	Optional<UserLessonProgress> findByAccountAndLesson(Account account, Lesson lesson);
}
