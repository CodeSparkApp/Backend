package de.dhbw.tinf22b6.codespark.api.repository.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringConversationRepository extends JpaRepository<Conversation, UUID> {
	Optional<Conversation> findByUserId(UUID id);
}
