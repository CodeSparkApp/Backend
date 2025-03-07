package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.MessageSenderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConversationMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	private MessageSenderType senderType;

	@Column(columnDefinition = "TEXT")
	private String message;

	public ConversationMessage(MessageSenderType senderType, String message) {
		this.senderType = senderType;
		this.message = message;
	}
}
