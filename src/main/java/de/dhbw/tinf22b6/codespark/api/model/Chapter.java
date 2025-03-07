package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Chapter {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, unique = true)
	private int orderIndex;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "chapter_id")
	private final List<Task> tasks = new ArrayList<>();

	public Chapter(String title, int orderIndex) {
		this.title = title;
		this.orderIndex = orderIndex;
	}
}
