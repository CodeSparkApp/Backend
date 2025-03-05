package de.dhbw.tinf22b6.codespark.api.exception;

public class InvalidRefreshTokenException extends RuntimeException {
	public InvalidRefreshTokenException(String message) {
		super(message);
	}
}
