package de.dhbw.tinf22b6.codespark.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.exception.*;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.Role;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RequestPasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.AccountDetailsResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.repository.RoleRepository;
import de.dhbw.tinf22b6.codespark.api.repository.VerificationTokenRepository;
import de.dhbw.tinf22b6.codespark.api.service.common.PredefinedUserRole;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final RoleRepository roleRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final Cloudinary cloudinary;
	private final Environment env;

	public AccountServiceImpl(@Autowired AccountRepository accountRepository,
							  @Autowired VerificationTokenRepository verificationTokenRepository,
							  @Autowired RoleRepository roleRepository,
							  @Autowired EmailService emailService,
							  @Autowired PasswordEncoder passwordEncoder,
							  @Autowired Cloudinary cloudinary,
							  @Autowired Environment env) {
		this.accountRepository = accountRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.roleRepository = roleRepository;
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
				account.getProfileImageUrl(),
				account.getCreationDate()
		);
	}

	@Override
	public void createAccount(AccountCreateRequest request) {
		if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new AccountAlreadyExistsException("An account with this email already exists.");
		}

		if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new AccountAlreadyExistsException("This username is already taken.");
		}

		Role userRole = roleRepository.findByName(PredefinedUserRole.USER.getName())
				.orElseThrow(() -> new EntryNotFoundException("The requested role does not exists."));

		String encodedPassword = passwordEncoder.encode(request.getPassword());
		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		Account account = new Account(request.getUsername(), request.getEmail(), encodedPassword, false,
				Set.of(userRole), now, now);
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
						.orElseThrow(() -> new InvalidVerificationTokenException("The verification link is invalid or has expired."));

		if (verificationToken.isExpired()) {
			verificationTokenRepository.delete(verificationToken);
			throw new ExpiredVerificationTokenException("Your email verification link has expired. Please request a new one.");
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
			// Note: Don't notify client that account with this email exists
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
						.orElseThrow(() -> new InvalidVerificationTokenException("The verification link is invalid or has expired."));

		if (verificationToken.isExpired()) {
			verificationTokenRepository.delete(verificationToken);
			throw new ExpiredVerificationTokenException("Your password reset link has expired. Please request a new one.");
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
			throw new ImageUploadException("An error occurred while trying to upload the profile image.");
		}
		String imageUrl = uploadResult.get("secure_url").toString();

		// Update account with new profile image URL
		account.setProfileImageUrl(imageUrl);
		accountRepository.save(account);

		return new UploadImageResponse(imageUrl);
	}
}
