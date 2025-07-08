package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
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

	@Column(length = 512)
	private String profileImageUrl;

	@Column(nullable = false)
	private boolean verified;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "account_role",
			joinColumns = @JoinColumn(name = "account_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();

	@Column(nullable = false, updatable = false)
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private LocalDateTime lastLogin;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private Conversation conversation;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<VerificationToken> verificationTokens = new ArrayList<>();

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private ExamDate examDate;

	public Account(String username, String email, String password, boolean verified, Set<Role> roles,
				   LocalDateTime creationDate, LocalDateTime lastLogin) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.verified = verified;
		this.roles = roles;
		this.creationDate = creationDate;
		this.lastLogin = lastLogin;
	}
}
