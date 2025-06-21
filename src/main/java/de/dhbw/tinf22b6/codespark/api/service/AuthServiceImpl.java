package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.exception.InvalidAccountCredentialsException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidRefreshTokenException;
import de.dhbw.tinf22b6.codespark.api.exception.UnverifiedAccountException;
import de.dhbw.tinf22b6.codespark.api.model.Account;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RefreshTokenRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.repository.AccountRepository;
import de.dhbw.tinf22b6.codespark.api.security.JwtUtil;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AuthServiceImpl implements AuthService {
	private final AccountRepository accountRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	public AuthServiceImpl(@Autowired AccountRepository accountRepository,
						   @Autowired JwtUtil jwtUtil,
						   @Autowired PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public TokenResponse loginAccount(LoginRequest request) {
		Account account = accountRepository.findByUsername(request.getUsernameOrEmail())
				.or(() -> accountRepository.findByEmail(request.getUsernameOrEmail()))
				.orElseThrow(() -> new InvalidAccountCredentialsException("The username or password you entered is incorrect."));

		if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
			throw new InvalidAccountCredentialsException("The username or password you entered is incorrect.");
		}

		if (!account.isVerified()) {
			throw new UnverifiedAccountException("Please verify your email before logging in.");
		}

		String accessToken = jwtUtil.generateAccessToken(account.getUsername());
		String refreshToken = jwtUtil.generateRefreshToken(account.getUsername());

		account.setLastLogin(LocalDateTime.now(ZoneOffset.UTC));
		accountRepository.save(account);

		return new TokenResponse(accessToken, refreshToken);
	}

	@Override
	public TokenResponse refreshAccessToken(RefreshTokenRequest request) {
		if (!jwtUtil.validateToken(request.getRefreshToken())) {
			throw new InvalidRefreshTokenException("Your session has expired. Please log in again.");
		}

		String username = jwtUtil.extractUsername(request.getRefreshToken());
		Account account = accountRepository.findByUsername(username)
				.orElseThrow(() -> new InvalidAccountCredentialsException("No account with username " + username + "was found."));

		String newAccessToken = jwtUtil.generateAccessToken(username);

		account.setLastLogin(LocalDateTime.now(ZoneOffset.UTC));
		accountRepository.save(account);

		return new TokenResponse(newAccessToken, request.getRefreshToken());
	}
}
