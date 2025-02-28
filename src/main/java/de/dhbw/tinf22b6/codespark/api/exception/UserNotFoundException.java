package de.dhbw.tinf22b6.codespark.api.exception;

public class UserNotFoundException extends Exception {
	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}
}
