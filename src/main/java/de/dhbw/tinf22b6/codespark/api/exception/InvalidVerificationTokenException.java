package de.dhbw.tinf22b6.codespark.api.exception;

public class InvalidVerificationTokenException extends RuntimeException {
	public InvalidVerificationTokenException(String message) {
		super(message);
	}
}
