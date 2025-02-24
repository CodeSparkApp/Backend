package de.dhbw.tinf22b6.codespark.api.exception;

public class AccountAlreadyExistsException extends Exception {
	public AccountAlreadyExistsException() {
		super();
	}

	public AccountAlreadyExistsException(String message) {
		super(message);
	}
}
