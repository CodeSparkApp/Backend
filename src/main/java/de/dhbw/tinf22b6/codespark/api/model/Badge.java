package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.BadgeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Badge {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true)
	private BadgeType type;

	@Column(nullable = false)
	private String icon;

	public Badge(String name, String description, BadgeType type, String icon) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.icon = icon;
	}
}
