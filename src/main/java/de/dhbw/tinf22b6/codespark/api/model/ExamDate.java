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
public class ExamDate {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private LocalDateTime date;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", unique = true, nullable = false)
	private Account account;

	public ExamDate(LocalDateTime date) {
		this.date = date;
	}
}

