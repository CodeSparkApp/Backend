package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Account account;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "conversation_id")
	private final List<ConversationMessage> messages = new ArrayList<>();

	public Conversation() {}

	public Conversation(Account account) {
		this.account = account;
	}

	public void addMessage(ConversationMessage message) {
		this.messages.add(message);
	}
}
