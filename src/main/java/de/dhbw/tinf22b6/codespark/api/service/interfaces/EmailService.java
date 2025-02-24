package de.dhbw.tinf22b6.codespark.api.service.interfaces;

public interface EmailService {
	void sendVerificationEmail(String to, String token);
	void sendPasswordResetEmail(String to, String token);
}
