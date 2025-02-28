package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
public class VerificationToken {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String token;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Account account;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VerificationTokenType type;

	@Column(nullable = false)
	private Instant expiryDate;

	public VerificationToken() {}

	public VerificationToken(String token, Account account, VerificationTokenType type, Instant expiryDate) {
		this.token = token;
		this.account = account;
		this.type = type;
		this.expiryDate = expiryDate;
	}

	public boolean isExpired() {
		return Instant.now().isAfter(expiryDate);
	}
}
