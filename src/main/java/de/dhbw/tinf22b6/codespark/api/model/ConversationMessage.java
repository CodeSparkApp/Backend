package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.SenderType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class ConversationMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private SenderType senderType;

	@Column(columnDefinition = "TEXT")
	private String message;

	public ConversationMessage() {}

	public ConversationMessage(SenderType senderType, String message) {
		this.senderType = senderType;
		this.message = message;
	}
}
