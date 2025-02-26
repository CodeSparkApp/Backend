package de.dhbw.tinf22b6.codespark.api.service;

import de.dhbw.tinf22b6.codespark.api.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;
	private final Environment env;

	public EmailServiceImpl(@Autowired JavaMailSender mailSender,
							@Autowired Environment env) {
		this.mailSender = mailSender;
		this.env = env;
	}

	@Override
	public void sendVerificationEmail(String to, String token) {
		String verificationLink = "http://localhost:8080/api/v1/account/verify?token=" + token;
		String message = "Click the link to verify your account: " + verificationLink;

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(to);
		email.setFrom(String.format("CodeSpark <%s>", env.getRequiredProperty("smtp.email")));
		email.setSubject("Email Verification");
		email.setText(message);

		mailSender.send(email);
	}

	@Override
	public void sendVerificationConfirmationEmail(String to) {
		String message = "Your account has been verified successfully.";

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(to);
		email.setFrom(String.format("CodeSpark <%s>", env.getRequiredProperty("smtp.email")));
		email.setSubject("Verification Confirmation");
		email.setText(message);

		mailSender.send(email);
	}

	@Override
	public void sendPasswordResetEmail(String to, String token) {
		String resetLink = "http://localhost:8080/api/v1/account/reset-password?token=" + token;
		String message = "Click here to reset your password: " + resetLink;

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(to);
		email.setFrom(String.format("CodeSpark <%s>", env.getRequiredProperty("smtp.email")));
		email.setSubject("Password Reset Request");
		email.setText(message);

		mailSender.send(email);
	}

	@Override
	public void sendPasswordResetConfirmationEmail(String to) {
		String message = "The password for your account has been changed successfully.";

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(to);
		email.setFrom(String.format("CodeSpark <%s>", env.getRequiredProperty("smtp.email")));
		email.setSubject("Password Reset Confirmation");
		email.setText(message);

		mailSender.send(email);
	}
}
