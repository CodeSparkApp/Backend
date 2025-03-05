package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AccountService {
	void createAccount(AccountCreateRequest request);
	void verifyEmail(String token);
	void requestPasswordReset(String email);
	void resetPassword(PasswordResetRequest request);
	UUID getAccountIdByUsername(String username);
	UploadImageResponse updateProfileImage(UUID accountId, MultipartFile file);
}
