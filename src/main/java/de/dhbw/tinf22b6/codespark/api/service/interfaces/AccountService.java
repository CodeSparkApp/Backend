package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RequestPasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.AccountDetailsResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AccountService {
	AccountDetailsResponse getAccountDetails(Account account);
	void createAccount(AccountCreateRequest request);
	void verifyEmail(String token);
	void requestPasswordReset(RequestPasswordResetRequest request);
	void resetPassword(PasswordResetRequest request);
	UploadImageResponse updateProfileImage(Account account, MultipartFile file);
}
