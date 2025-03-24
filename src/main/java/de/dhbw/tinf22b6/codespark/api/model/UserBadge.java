package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserBadge {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Account account;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Badge badge;

	@Column(nullable = false)
	private LocalDateTime receiveDate;

	public UserBadge(Account account, Badge badge, LocalDateTime receiveDate) {
		this.account = account;
		this.badge = badge;
		this.receiveDate = receiveDate;
	}
}
