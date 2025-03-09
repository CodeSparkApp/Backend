package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class VerificationToken {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String token;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VerificationTokenType type;

	@Column(nullable = false)
	private Instant expiryDate;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Account account;

	public VerificationToken(String token, VerificationTokenType type, Instant expiryDate, Account account) {
		this.token = token;
		this.type = type;
		this.expiryDate = expiryDate;
		this.account = account;
	}

	public boolean isExpired() {
		return Instant.now().isAfter(expiryDate);
	}
}
