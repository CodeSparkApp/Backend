package de.dhbw.tinf22b6.codespark.api.exception;

public class InvalidAccountCredentialsException extends Exception {
	public InvalidAccountCredentialsException() {
		super();
	}

	public InvalidAccountCredentialsException(String message) {
		super(message);
	}
}
