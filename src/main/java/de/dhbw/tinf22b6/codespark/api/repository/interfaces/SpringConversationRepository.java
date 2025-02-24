package de.dhbw.tinf22b6.codespark.api.repository.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringConversationRepository extends JpaRepository<Conversation, Long> {
	Optional<Conversation> findByUserId(Long id);
}
