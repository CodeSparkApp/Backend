package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import de.dhbw.tinf22b6.codespark.api.repository.interfaces.SpringConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConversationRepository {
	private final SpringConversationRepository conversationRepository;

	public ConversationRepository(@Autowired SpringConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	public Optional<Conversation> findByAccount(Account account) {
		return conversationRepository.findByAccount(account);
	}

	public Conversation save(Conversation conversation) {
		return conversationRepository.save(conversation);
	}
}
