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

import java.util.UUID;

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
				.orElseThrow(() -> new InvalidAccountCredentialsException("Invalid username or password"));

		if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
			throw new InvalidAccountCredentialsException("Invalid username or password");
		}

		if (!account.isVerified()) {
			throw new UnverifiedAccountException("The current account is not verified");
		}

		String accessToken = jwtUtil.generateAccessToken(account.getId(), account.getRole().name());
		String refreshToken = jwtUtil.generateRefreshToken(account.getId());

		return new TokenResponse(accessToken, refreshToken);
	}

	@Override
	public TokenResponse refreshAccessToken(RefreshTokenRequest request) {
		if (!jwtUtil.validateToken(request.getRefreshToken())) {
			throw new InvalidRefreshTokenException("The refresh token is invalid or has expired");
		}

		UUID accountID = jwtUtil.extractAccountID(request.getRefreshToken());
		String role = jwtUtil.extractRole(request.getRefreshToken());
		String newAccessToken = jwtUtil.generateAccessToken(accountID, role);
		return new TokenResponse(newAccessToken, request.getRefreshToken());
	}
}
