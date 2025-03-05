package de.dhbw.tinf22b6.codespark.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.exception.AccountAlreadyExistsException;
import de.dhbw.tinf22b6.codespark.api.exception.ImageUploadException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidVerificationTokenException;
import de.dhbw.tinf22b6.codespark.api.exception.UserNotFoundException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.repository.VerificationTokenRepository;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

	public AccountServiceImpl(@Autowired AccountRepository accountRepository,
							  @Autowired VerificationTokenRepository verificationTokenRepository,
							  @Autowired EmailService emailService,
							  @Autowired PasswordEncoder passwordEncoder,
							  @Autowired Cloudinary cloudinary) {
		this.accountRepository = accountRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.cloudinary = cloudinary;
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
		Instant expiryDate = Instant.now().plusSeconds(86400); // 1 Day
		verificationTokenRepository.save(new VerificationToken(token, account, VerificationTokenType.EMAIL_VERIFICATION, expiryDate));

		emailService.sendVerificationEmail(account.getEmail(), token);
	}

	@Override
	public void verifyEmail(String token) {
		Optional<VerificationToken> optionalToken =
				verificationTokenRepository.findByTokenAndType(token, VerificationTokenType.EMAIL_VERIFICATION);

		if (optionalToken.isEmpty()) {
			throw new InvalidVerificationTokenException("The verification link is invalid or has expired");
		}

		VerificationToken verificationToken = optionalToken.get();
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
	public void requestPasswordReset(String email) {
		Optional<Account> optionalAccount = accountRepository.findByEmail(email);
		if (optionalAccount.isEmpty()) {
			// Don't notify client that account with this email exists
			return;
		}

		Account account = optionalAccount.get();
		String token = UUID.randomUUID().toString();
		Instant expiryDate = Instant.now().plusSeconds(3600); // 1 Hour
		verificationTokenRepository.save(new VerificationToken(token, account, VerificationTokenType.PASSWORD_RESET, expiryDate));

		emailService.sendPasswordResetEmail(email, token);
	}

	@Override
	public void resetPassword(PasswordResetRequest request) {
		Optional<VerificationToken> optionalToken =
				verificationTokenRepository.findByTokenAndType(request.getVerificationToken(), VerificationTokenType.PASSWORD_RESET);

		if (optionalToken.isEmpty()) {
			throw new InvalidVerificationTokenException("The verification link is invalid or has expired");
		}

		VerificationToken verificationToken = optionalToken.get();
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
	public UUID getAccountIdByUsername(String username) {
		return accountRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No account was found with this username"))
				.getId();
	}

	@Override
	public UploadImageResponse updateProfileImage(UUID accountId, MultipartFile file) {
		Optional<Account> optionalAccount = accountRepository.findById(accountId);
		if (optionalAccount.isEmpty()) {
			throw new UserNotFoundException("No account was found with the provided information");
		}

		Account account = optionalAccount.get();

		// Upload file to Cloudinary
		Map<?, ?> uploadResult;
		try {
			uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
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
