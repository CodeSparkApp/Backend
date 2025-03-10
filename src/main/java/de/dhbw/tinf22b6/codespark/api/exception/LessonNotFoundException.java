package de.dhbw.tinf22b6.codespark.api.exception;

public class LessonNotFoundException extends RuntimeException {
	public LessonNotFoundException(String message) {
		super(message);
	}
}
