package de.dhbw.tinf22b6.codespark.api.model;

import de.dhbw.tinf22b6.codespark.api.common.TaskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TaskType type;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb", nullable = false)
	private String data;

	@ManyToOne
	@JoinColumn(name = "chapter_id")
	private Chapter chapter;

	public Task(TaskType type, String title, String description, String data, Chapter chapter) {
		this.type = type;
		this.title = title;
		this.description = description;
		this.data = data;
		this.chapter = chapter;
	}
}
