package de.dhbw.tinf22b6.codespark.api.exception;

public class ExpiredVerificationTokenException extends RuntimeException {
	public ExpiredVerificationTokenException(String message) {
		super(message);
	}
}
