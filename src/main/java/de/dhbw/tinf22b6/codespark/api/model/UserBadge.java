package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserBadge {
	@EmbeddedId
	private UserBadgeId id;

	@ManyToOne
	@MapsId("accountId")
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@ManyToOne
	@MapsId("badgeId")
	@JoinColumn(name = "badge_id", nullable = false)
	private Badge badge;

	@Column(nullable = false)
	private LocalDateTime receiveDate;

	public UserBadge(Account account, Badge badge, LocalDateTime receiveDate) {
		this.id = new UserBadgeId(account.getId(), badge.getId());
		this.account = account;
		this.badge = badge;
		this.receiveDate = receiveDate;
	}
}
