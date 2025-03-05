package de.dhbw.tinf22b6.codespark.api.exception;

public class AccountAlreadyExistsException extends RuntimeException {
	public AccountAlreadyExistsException(String message) {
		super(message);
	}
}
