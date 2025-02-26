package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ConversationRepository {
	private final SpringConversationRepository conversationRepository;

	public ConversationRepository(@Autowired SpringConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	public Optional<Conversation> findByUserId(UUID userId) {
		return conversationRepository.findByUserId(userId);
	}

	public Conversation save(Conversation conversation) {
		return conversationRepository.save(conversation);
	}
}
