package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Conversation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "conversation_id")
	private final List<ConversationMessage> messages = new ArrayList<>();

	public Conversation() {}

	public Conversation(Long userId) {
		this.userId = userId;
	}

	public void addMessage(ConversationMessage message) {
		this.messages.add(message);
	}
}
