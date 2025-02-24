package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.exception.AccountAlreadyExistsException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidAccountCredentialsException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidVerificationTokenException;
import de.dhbw.tinf22b6.codespark.api.exception.UnverifiedAccountException;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;

public interface AccountService {
	void createAccount(AccountCreateRequest request) throws AccountAlreadyExistsException;
	void verifyEmail(String token) throws InvalidVerificationTokenException;
	TokenResponse loginAccount(LoginRequest request) throws InvalidAccountCredentialsException, UnverifiedAccountException;
	void requestPasswordReset(String email);
	void resetPassword(PasswordResetRequest request) throws InvalidVerificationTokenException;
}
