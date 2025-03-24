package de.dhbw.tinf22b6.codespark.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.exception.AccountAlreadyExistsException;
import de.dhbw.tinf22b6.codespark.api.exception.ImageUploadException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidVerificationTokenException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RequestPasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.AccountDetailsResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.repository.VerificationTokenRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final Cloudinary cloudinary;
	private final Environment env;

	public AccountServiceImpl(@Autowired AccountRepository accountRepository,
							  @Autowired VerificationTokenRepository verificationTokenRepository,
							  @Autowired EmailService emailService,
							  @Autowired PasswordEncoder passwordEncoder,
							  @Autowired Cloudinary cloudinary,
							  @Autowired Environment env) {
		this.accountRepository = accountRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.cloudinary = cloudinary;
		this.env = env;
	}

	@Override
	public AccountDetailsResponse getAccountDetails(Account account) {
		return new AccountDetailsResponse(
				account.getId(),
				account.getUsername(),
				account.getEmail(),
				account.getProfileImageUrl()
		);
	}

	@Override
	public void createAccount(AccountCreateRequest request) {
		if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new AccountAlreadyExistsException("An account with this email already exists");
		}

		if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new AccountAlreadyExistsException("This username is already taken");
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());
		Account account = new Account(request.getUsername(), request.getEmail(), encodedPassword, UserRoleType.USER, false);
		accountRepository.save(account);

		String token = UUID.randomUUID().toString();
		Instant expiryDate = Instant.now().plusMillis(env.getRequiredProperty("auth.verification.email-token-expiration", Long.class));
		VerificationToken verificationToken = new VerificationToken(token, VerificationTokenType.EMAIL_VERIFICATION, expiryDate, account);
		verificationTokenRepository.save(verificationToken);

		emailService.sendVerificationEmail(account.getEmail(), token);
	}

	@Override
	public void verifyEmail(String token) {
		VerificationToken verificationToken =
				verificationTokenRepository.findByTokenAndType(token, VerificationTokenType.EMAIL_VERIFICATION)
						.orElseThrow(() -> new InvalidVerificationTokenException("The verification link is invalid or has expired"));

		if (verificationToken.isExpired()) {
			// TODO
			return;
		}

		Account account = verificationToken.getAccount();
		account.setVerified(true);
		accountRepository.save(account);
		verificationTokenRepository.delete(verificationToken);

		emailService.sendVerificationConfirmationEmail(account.getEmail());
	}

	@Override
	public void requestPasswordReset(RequestPasswordResetRequest request) {
		Optional<Account> optionalAccount = accountRepository.findByEmail(request.getEmail());
		if (optionalAccount.isEmpty()) {
			// Don't notify client that account with this email exists
			return;
		}

		Account account = optionalAccount.get();
		String token = UUID.randomUUID().toString();
		Instant expiryDate = Instant.now().plusSeconds(env.getRequiredProperty("auth.verification.password-token-expiration", Long.class));
		VerificationToken verificationToken = new VerificationToken(token, VerificationTokenType.PASSWORD_RESET, expiryDate, account);
		verificationTokenRepository.save(verificationToken);

		emailService.sendPasswordResetEmail(request.getEmail(), token);
	}

	@Override
	public void resetPassword(PasswordResetRequest request) {
		VerificationToken verificationToken =
				verificationTokenRepository.findByTokenAndType(request.getVerificationToken(), VerificationTokenType.PASSWORD_RESET)
						.orElseThrow(() -> new InvalidVerificationTokenException("The verification link is invalid or has expired"));

		if (verificationToken.isExpired()) {
			// TODO
			return;
		}

		Account account = verificationToken.getAccount();
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		accountRepository.save(account);
		verificationTokenRepository.delete(verificationToken);

		emailService.sendPasswordResetConfirmationEmail(account.getEmail());
	}

	@Override
	public UploadImageResponse updateProfileImage(Account account, MultipartFile file) {
		Map<?, ?> uploadOptions = ObjectUtils.asMap(
				"folder", "user-content/profile-images",
				"public_id", account.getId().toString(),
				"overwrite", true,
				"invalidate", true
		);

		Map<?, ?> uploadResult;
		try {
			uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
		} catch (IOException e) {
			throw new ImageUploadException("An error occurred while trying to upload the image");
		}
		String imageUrl = uploadResult.get("secure_url").toString();

		// Update account with new profile image URL
		account.setProfileImageUrl(imageUrl);
		accountRepository.save(account);

		return new UploadImageResponse(imageUrl);
	}
}
