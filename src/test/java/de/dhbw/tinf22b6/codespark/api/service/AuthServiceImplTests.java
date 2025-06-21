package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceImplTests {
	private AccountRepository accountRepository;
	private JwtUtil jwtUtil;
	private PasswordEncoder passwordEncoder;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		this.accountRepository = mock(AccountRepository.class);
		this.jwtUtil = mock(JwtUtil.class);
		this.passwordEncoder = mock(PasswordEncoder.class);

		this.authService = new  AuthServiceImpl(accountRepository, jwtUtil, passwordEncoder);
	}

	@Test
	void loginAccount_shouldReturnTokenResponse_whenCredentialsAreValid() {
		LoginRequest request = new LoginRequest("testuser", "password");
		Account account = new Account();
		account.setUsername("testuser");
		account.setPassword("encoded");
		account.setVerified(true);
		account.setRole(UserRoleType.USER);

		when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
		when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
		when(jwtUtil.generateAccessToken("testuser")).thenReturn("access-token");
		when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refresh-token");

		TokenResponse response = authService.loginAccount(request);

		assertThat(response.getAccessToken()).isEqualTo("access-token");
		assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
	}

	@Test
	void loginAccount_shouldThrow_whenUserNotFound() {
		LoginRequest request = new LoginRequest("unknown", "password");

		when(accountRepository.findByUsername("unknown")).thenReturn(Optional.empty());
		when(accountRepository.findByEmail("unknown")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.loginAccount(request))
				.isInstanceOf(InvalidAccountCredentialsException.class);
	}

	@Test
	void loginAccount_shouldThrow_whenPasswordInvalid() {
		LoginRequest request = new LoginRequest("testuser", "wrongpass");
		Account account = new Account();
		account.setUsername("testuser");
		account.setPassword("encoded");

		when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
		when(passwordEncoder.matches("wrongpass", "encoded")).thenReturn(false);

		assertThatThrownBy(() -> authService.loginAccount(request))
				.isInstanceOf(InvalidAccountCredentialsException.class);
	}

	@Test
	void loginAccount_shouldThrow_whenAccountNotVerified() {
		LoginRequest request = new LoginRequest("testuser", "password");
		Account account = new Account();
		account.setUsername("testuser");
		account.setPassword("encoded");
		account.setVerified(false);

		when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));
		when(passwordEncoder.matches("password", "encoded")).thenReturn(true);

		assertThatThrownBy(() -> authService.loginAccount(request))
				.isInstanceOf(UnverifiedAccountException.class);
	}

	@Test
	void refreshAccessToken_shouldReturnNewAccessToken_whenValid() {
		RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
		Account account = new Account();
		account.setUsername("testuser");
		account.setPassword("encoded");
		account.setVerified(false);

		when(jwtUtil.validateToken("refresh-token")).thenReturn(true);
		when(jwtUtil.extractUsername("refresh-token")).thenReturn("testuser");
		when(jwtUtil.generateAccessToken("testuser")).thenReturn("new-access-token");
		when(accountRepository.findByUsername("testuser")).thenReturn(Optional.of(account));

		TokenResponse response = authService.refreshAccessToken(request);

		assertThat(response.getAccessToken()).isEqualTo("new-access-token");
		assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
	}

	@Test
	void refreshAccessToken_shouldThrow_whenTokenInvalid() {
		RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

		when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

		assertThatThrownBy(() -> authService.refreshAccessToken(request))
				.isInstanceOf(InvalidRefreshTokenException.class);
	}
}
