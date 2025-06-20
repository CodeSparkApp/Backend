package de.dhbw.tinf22b6.codespark.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import de.dhbw.tinf22b6.codespark.api.common.UserRoleType;
import de.dhbw.tinf22b6.codespark.api.common.VerificationTokenType;
import de.dhbw.tinf22b6.codespark.api.exception.AccountAlreadyExistsException;
import de.dhbw.tinf22b6.codespark.api.exception.ExpiredVerificationTokenException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AccountServiceImplTests {
	private AccountRepository accountRepository;
	private VerificationTokenRepository verificationTokenRepository;
	private EmailService emailService;
	private PasswordEncoder passwordEncoder;
	private Cloudinary cloudinary;
	private Environment env;
	private MultipartFile file;
	private AccountService accountService;

	@BeforeEach
	void setUp() {
		accountRepository = mock(AccountRepository.class);
		verificationTokenRepository = mock(VerificationTokenRepository.class);
		emailService = mock(EmailService.class);
		passwordEncoder = mock(PasswordEncoder.class);
		cloudinary = mock(Cloudinary.class);
		env = mock(Environment.class);
		file = mock(MultipartFile.class);

		accountService = new AccountServiceImpl(
				accountRepository, verificationTokenRepository,  emailService,
				passwordEncoder, cloudinary, env
		);
	}

	@Test
	void getAccountDetails_shouldReturnCorrectResponse() {
		Account account = new Account("user", "user@example.com", "pass",
				UserRoleType.USER, true, LocalDateTime.now(), LocalDateTime.now());
		account.setId(UUID.randomUUID());
		account.setProfileImageUrl("http://image.url");

		AccountDetailsResponse response = accountService.getAccountDetails(account);

		assertThat(response.getUsername()).isEqualTo("user");
		assertThat(response.getEmail()).isEqualTo("user@example.com");
		assertThat(response.getProfileImageUrl()).isEqualTo("http://image.url");
	}

	@Test
	void createAccount_shouldCreateNewAccount() {
		AccountCreateRequest request = new AccountCreateRequest("user", "user@example.com", "password");
		when(accountRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
		when(accountRepository.findByUsername("user")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("hashed");
		when(env.getRequiredProperty(any(), eq(Long.class))).thenReturn(1000L);

		accountService.createAccount(request);

		verify(accountRepository).save(any(Account.class));
		verify(verificationTokenRepository).save(any(VerificationToken.class));
		verify(emailService).sendVerificationEmail(eq("user@example.com"), any());
	}

	@Test
	void createAccount_shouldThrowIfEmailExists() {
		AccountCreateRequest request = new AccountCreateRequest("user", "user@example.com", "password");
		when(accountRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mock(Account.class)));

		assertThatThrownBy(() -> accountService.createAccount(request))
				.isInstanceOf(AccountAlreadyExistsException.class);
	}

	@Test
	void createAccount_shouldThrowIfUsernameExists() {
		AccountCreateRequest request = new AccountCreateRequest("user", "user@example.com", "password");
		when(accountRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
		when(accountRepository.findByUsername("user")).thenReturn(Optional.of(mock(Account.class)));

		assertThatThrownBy(() -> accountService.createAccount(request))
				.isInstanceOf(AccountAlreadyExistsException.class);
	}

	@Test
	void verifyEmail_shouldVerifyIfTokenValid() {
		Account account = new Account();
		VerificationToken token = new VerificationToken("token", VerificationTokenType.EMAIL_VERIFICATION, Instant.now().plusSeconds(60), account);
		when(verificationTokenRepository.findByTokenAndType("token", VerificationTokenType.EMAIL_VERIFICATION))
				.thenReturn(Optional.of(token));

		accountService.verifyEmail("token");

		verify(accountRepository).save(account);
		verify(emailService).sendVerificationConfirmationEmail(account.getEmail());
		verify(verificationTokenRepository).delete(token);
	}

	@Test
	void verifyEmail_shouldThrowIfTokenInvalid() {
		when(verificationTokenRepository.findByTokenAndType(any(), any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.verifyEmail("invalid"))
				.isInstanceOf(InvalidVerificationTokenException.class);
	}

	@Test
	void verifyEmail_shouldThrowIfTokenExpired() {
		VerificationToken token = new VerificationToken("token", VerificationTokenType.EMAIL_VERIFICATION, Instant.now().minusSeconds(60), new Account());
		when(verificationTokenRepository.findByTokenAndType("token", VerificationTokenType.EMAIL_VERIFICATION))
				.thenReturn(Optional.of(token));

		assertThatThrownBy(() -> accountService.verifyEmail("token"))
				.isInstanceOf(ExpiredVerificationTokenException.class);

		verify(verificationTokenRepository).delete(token);
	}

	@Test
	void requestPasswordReset_shouldDoNothingIfEmailNotFound() {
		when(accountRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

		accountService.requestPasswordReset(new RequestPasswordResetRequest("notfound@example.com"));

		verifyNoInteractions(emailService);
	}

	@Test
	void requestPasswordReset_shouldSendResetEmail() {
		Account account = new Account();
		when(accountRepository.findByEmail("user@example.com")).thenReturn(Optional.of(account));
		when(env.getRequiredProperty(any(), eq(Long.class))).thenReturn(1000L);

		accountService.requestPasswordReset(new RequestPasswordResetRequest("user@example.com"));

		verify(emailService).sendPasswordResetEmail(eq("user@example.com"), any());
	}

	@Test
	void resetPassword_shouldUpdatePassword() {
		Account account = new Account();
		VerificationToken token = new VerificationToken("token", VerificationTokenType.PASSWORD_RESET, Instant.now().plusSeconds(60), account);
		when(verificationTokenRepository.findByTokenAndType("token", VerificationTokenType.PASSWORD_RESET))
				.thenReturn(Optional.of(token));
		when(passwordEncoder.encode("newpass")).thenReturn("encoded");

		accountService.resetPassword(new PasswordResetRequest("token", "newpass"));

		verify(accountRepository).save(account);
		verify(verificationTokenRepository).delete(token);
		verify(emailService).sendPasswordResetConfirmationEmail(account.getEmail());
	}

	@Test
	void resetPassword_shouldThrowIfTokenInvalid() {
		when(verificationTokenRepository.findByTokenAndType(any(), any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> accountService.resetPassword(new PasswordResetRequest("token", "pass")))
				.isInstanceOf(InvalidVerificationTokenException.class);
	}

	@Test
	void resetPassword_shouldThrowIfTokenExpired() {
		VerificationToken token = new VerificationToken("token", VerificationTokenType.PASSWORD_RESET, Instant.now().minusSeconds(60), new Account());
		when(verificationTokenRepository.findByTokenAndType(any(), any())).thenReturn(Optional.of(token));

		assertThatThrownBy(() -> accountService.resetPassword(new PasswordResetRequest("token", "pass")))
				.isInstanceOf(ExpiredVerificationTokenException.class);

		verify(verificationTokenRepository).delete(token);
	}

	@Test
	void updateProfileImage_shouldUploadAndReturnUrl() throws Exception {
		Account account = new Account();
		account.setId(UUID.randomUUID());
		byte[] fileBytes = new byte[]{1, 2, 3};

		when(file.getBytes()).thenReturn(fileBytes);

		Uploader uploader = mock(Uploader.class);
		when(cloudinary.uploader()).thenReturn(uploader);
		when(uploader.upload(eq(fileBytes), anyMap())).thenReturn(Map.of("secure_url", "http://new.image"));

		UploadImageResponse response = accountService.updateProfileImage(account, file);

		assertThat(response.getImageUrl()).isEqualTo("http://new.image");
		verify(accountRepository).save(account);
	}

	@Test
	void updateProfileImage_shouldThrowOnIOException() throws Exception {
		Account account = new Account();
		account.setId(UUID.randomUUID());

		when(file.getBytes()).thenThrow(new IOException("fail"));

		assertThatThrownBy(() -> accountService.updateProfileImage(account, file))
				.isInstanceOf(ImageUploadException.class);
	}
}
