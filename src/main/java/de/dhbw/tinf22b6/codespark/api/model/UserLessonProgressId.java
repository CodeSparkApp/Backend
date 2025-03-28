package de.dhbw.tinf22b6.codespark.api.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserLessonProgressId implements Serializable {
	private UUID accountId;
	private UUID lessonId;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		UserLessonProgressId that = (UserLessonProgressId) o;
		return Objects.equals(accountId, that.accountId) && Objects.equals(lessonId, that.lessonId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId, lessonId);
	}
}
