package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceImplTests {
	private JavaMailSender mailSender;
	private Environment env;
	private EmailService emailService;

	@BeforeEach
	void setUp() {
		mailSender = mock(JavaMailSender.class);
		env = mock(Environment.class);

		emailService = new EmailServiceImpl(mailSender, env);

		when(env.getRequiredProperty("smtp.email")).thenReturn("noreply@codespark.dev");
		when(env.getRequiredProperty("app.base-url")).thenReturn("http://localhost:8080");
	}

	@Test
	void sendVerificationEmail_shouldSendCorrectEmail() {
		String to = "user@example.com";
		String token = "abc123";

		emailService.sendVerificationEmail(to, token);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());

		SimpleMailMessage email = captor.getValue();
		assertThat(email.getTo()).containsExactly(to);
		assertThat(email.getFrom()).isEqualTo("CodeSpark <noreply@codespark.dev>");
		assertThat(email.getSubject()).isEqualTo("Email Verification");
		assertThat(email.getText()).contains("http://localhost:8080/api/v1/account/verify?token=abc123");
	}

	@Test
	void sendVerificationConfirmationEmail_shouldSendCorrectEmail() {
		String to = "user@example.com";

		emailService.sendVerificationConfirmationEmail(to);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());

		SimpleMailMessage email = captor.getValue();
		assertThat(email.getTo()).containsExactly(to);
		assertThat(email.getFrom()).isEqualTo("CodeSpark <noreply@codespark.dev>");
		assertThat(email.getSubject()).isEqualTo("Verification Confirmation");
		assertThat(email.getText()).contains("verified successfully");
	}

	@Test
	void sendPasswordResetEmail_shouldSendCorrectEmail() {
		String to = "user@example.com";
		String token = "reset123";

		emailService.sendPasswordResetEmail(to, token);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());

		SimpleMailMessage email = captor.getValue();
		assertThat(email.getTo()).containsExactly(to);
		assertThat(email.getFrom()).isEqualTo("CodeSpark <noreply@codespark.dev>");
		assertThat(email.getSubject()).isEqualTo("Password Reset Request");
		assertThat(email.getText()).contains("http://localhost:8080/api/v1/account/reset-password?token=reset123");
	}

	@Test
	void sendPasswordResetConfirmationEmail_shouldSendCorrectEmail() {
		String to = "user@example.com";

		emailService.sendPasswordResetConfirmationEmail(to);

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());

		SimpleMailMessage email = captor.getValue();
		assertThat(email.getTo()).containsExactly(to);
		assertThat(email.getFrom()).isEqualTo("CodeSpark <noreply@codespark.dev>");
		assertThat(email.getSubject()).isEqualTo("Password Reset Confirmation");
		assertThat(email.getText()).contains("has been changed successfully");
	}
}
