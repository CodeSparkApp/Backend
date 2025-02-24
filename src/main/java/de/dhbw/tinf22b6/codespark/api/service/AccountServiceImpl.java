package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.exception.AccountAlreadyExistsException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidAccountCredentialsException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidVerificationTokenException;
import de.dhbw.tinf22b6.codespark.api.exception.UnverifiedAccountException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.security.JwtUtil;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AccountServiceImpl(@Autowired AccountRepository accountRepository,
							  @Autowired EmailService emailService,
							  @Autowired PasswordEncoder passwordEncoder,
							  @Autowired JwtUtil jwtUtil) {
		this.accountRepository = accountRepository;
		this.emailService = emailService;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
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
		String token = UUID.randomUUID().toString();
		Account account = new Account(request.getUsername(), request.getEmail(), encodedPassword, UserRoleType.USER, false, token);

		accountRepository.save(account);

		emailService.sendVerificationEmail(account.getEmail(), token);
	}

	@Override
	public void verifyEmail(String token) throws InvalidVerificationTokenException {
		Optional<Account> optionalAccount = accountRepository.findByVerificationToken(token);
		if (optionalAccount.isEmpty()) {
			throw new InvalidVerificationTokenException();
		}

		Account account = optionalAccount.get();
		account.setVerified(true);
		account.setVerificationToken(null);
		accountRepository.save(account);
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
			// Don't notify frontend that account with this email exists
			return;
		}

		Account account = optionalAccount.get();
		String resetToken = UUID.randomUUID().toString();
		account.setVerificationToken(resetToken);
		accountRepository.save(account);
		emailService.sendPasswordResetEmail(email, resetToken);
	}

	@Override
	public void resetPassword(PasswordResetRequest request) throws InvalidVerificationTokenException {
		Optional<Account> optionalAccount = accountRepository.findByVerificationToken(request.getVerificationToken());
		if (optionalAccount.isEmpty()) {
			throw new InvalidVerificationTokenException();
		}

		Account account = optionalAccount.get();
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		account.setVerificationToken(null);
		accountRepository.save(account);
	}
}
