package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private UserRoleType role;

	@Column(length = 512)
	private String profileImageUrl;

	@Column(nullable = false)
	private boolean verified;

	public Account() {}

	public Account(String username, String email, String password, UserRoleType role, boolean verified) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
		this.verified = verified;
	}
}
