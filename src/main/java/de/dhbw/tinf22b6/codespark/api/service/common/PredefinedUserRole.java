package de.dhbw.tinf22b6.codespark.api.service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedUserRole {
	USER("USER"),
	ADMIN("ADMIN");

	private final String name;
}
