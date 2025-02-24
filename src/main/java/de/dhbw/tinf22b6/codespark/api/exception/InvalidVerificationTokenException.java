package de.dhbw.tinf22b6.codespark.api.exception;

public class InvalidVerificationTokenException extends Exception {
	public InvalidVerificationTokenException() {
		super();
	}

	public InvalidVerificationTokenException(String message) {
		super(message);
	}
}
