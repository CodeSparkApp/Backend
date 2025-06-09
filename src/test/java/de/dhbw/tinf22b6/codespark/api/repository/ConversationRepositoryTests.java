package de.dhbw.tinf22b6.codespark.api.repository;

import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Conversation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ConversationRepositoryTests {
	@Autowired
	private ConversationRepository conversationRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void testFindByAccount_shouldReturnConversation() {
		Account account = new Account("user1", "user1@example.com", "password", UserRoleType.USER, true);
		account = accountRepository.save(account);

		Conversation conversation = new Conversation(account);
		conversationRepository.save(conversation);

		Optional<Conversation> result = conversationRepository.findByAccount(account);

		assertThat(result).isPresent();
		assertThat(result.get().getAccount().getUsername()).isEqualTo("user1");
	}
}
