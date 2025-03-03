package de.dhbw.tinf22b6.codespark.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.exception.*;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.model.VerificationToken;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.repository.VerificationTokenRepository;
import de.dhbw.tinf22b6.codespark.api.security.JwtUtil;
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
	private final JwtUtil jwtUtil;
	private final Cloudinary cloudinary;

	public AccountServiceImpl(@Autowired AccountRepository accountRepository,
							  @Autowired VerificationTokenRepository verificationTokenRepository,
							  @Autowired EmailService emailService,
							  @Autowired PasswordEncoder passwordEncoder,
							  @Autowired JwtUtil jwtUtil,
							  @Autowired Cloudinary cloudinary) {
		this.accountRepository = accountRepository;
		this.verificationTokenRepository = verificationTokenRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.cloudinary = cloudinary;
	}

	@Override
	public void createAccount(AccountCreateRequest request) throws AccountAlreadyExistsException {
		if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new AccountAlreadyExistsException("Email already in use");
		}

		if (accountRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new AccountAlreadyExistsException("Username already exists");
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
	public void verifyEmail(String token) throws InvalidVerificationTokenException {
		Optional<VerificationToken> optionalToken =
				verificationTokenRepository.findByTokenAndType(token, VerificationTokenType.EMAIL_VERIFICATION);

		if (optionalToken.isEmpty()) {
			throw new InvalidVerificationTokenException("Invalid verification token");
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
	public TokenResponse loginAccount(LoginRequest request) throws InvalidAccountCredentialsException, UnverifiedAccountException {
		Optional<Account> optionalAccount = accountRepository.findByUsername(request.getUsernameOrEmail())
				.or(() -> accountRepository.findByEmail(request.getUsernameOrEmail()));

		if ((optionalAccount.isEmpty()) || (!passwordEncoder.matches(request.getPassword(), optionalAccount.get().getPassword()))) {
			throw new InvalidAccountCredentialsException();
		}

		Account account = optionalAccount.get();
		if (!account.isVerified()) {
			throw new UnverifiedAccountException();
		}

		String accessToken = jwtUtil.generateAccessToken(account.getUsername(), account.getRole().name());
		String refreshToken = jwtUtil.generateRefreshToken(account.getUsername());

		return new TokenResponse(accessToken, refreshToken);
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
	public void resetPassword(PasswordResetRequest request) throws InvalidVerificationTokenException {
		Optional<VerificationToken> optionalToken =
				verificationTokenRepository.findByTokenAndType(request.getVerificationToken(), VerificationTokenType.PASSWORD_RESET);

		if (optionalToken.isEmpty()) {
			throw new InvalidVerificationTokenException("Invalid verification token");
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
	public UUID getAccountIdByUsername(String username) throws UsernameNotFoundException {
		return accountRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)))
				.getId();
	}

	@Override
	public UploadImageResponse updateProfileImage(UUID accountId, MultipartFile file) throws UserNotFoundException, IOException {
		Optional<Account> optionalAccount = accountRepository.findById(accountId);
		if (optionalAccount.isEmpty()) {
			throw new UserNotFoundException("Account not found");
		}

		Account account = optionalAccount.get();

		// Upload file to Cloudinary
		Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
		String imageUrl = uploadResult.get("secure_url").toString();

		// Update account with new profile image URL
		account.setProfileImageUrl(imageUrl);
		accountRepository.save(account);

		return new UploadImageResponse(imageUrl);
	}
}
