package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.exception.*;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface AccountService {
	void createAccount(AccountCreateRequest request) throws AccountAlreadyExistsException;
	void verifyEmail(String token) throws InvalidVerificationTokenException;
	TokenResponse loginAccount(LoginRequest request) throws InvalidAccountCredentialsException, UnverifiedAccountException;
	void requestPasswordReset(String email);
	void resetPassword(PasswordResetRequest request) throws InvalidVerificationTokenException;
	UUID getAccountIdByUsername(String username) throws UsernameNotFoundException;
	UploadImageResponse updateProfileImage(UUID accountId, MultipartFile file) throws UserNotFoundException, IOException;
}
