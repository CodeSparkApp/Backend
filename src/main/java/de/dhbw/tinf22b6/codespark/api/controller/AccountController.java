package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.exception.AccountAlreadyExistsException;
import de.dhbw.tinf22b6.codespark.api.exception.InvalidVerificationTokenException;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/account")
public class AccountController {
	private final AccountService accountService;

	public AccountController(@Autowired AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerAccount(@RequestBody AccountCreateRequest request) {
		try {
			accountService.createAccount(request);
			return ResponseEntity.status(HttpStatus.CREATED)
					.contentType(MediaType.TEXT_PLAIN)
					.body(null);
		} catch (AccountAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.contentType(MediaType.TEXT_PLAIN)
					.body(null);
		}
	}

	@GetMapping("/verify")
	public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
		try {
			accountService.verifyEmail(token);
			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Email verified successfully");
		} catch (InvalidVerificationTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.TEXT_PLAIN)
					.body(null);
		}
	}

	@PostMapping("/request-reset")
	public ResponseEntity<?> requestPasswordReset(@RequestBody String email) {
		accountService.requestPasswordReset(email);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.TEXT_PLAIN)
				.body("Password reset email sent");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
		try {
			accountService.resetPassword(request);
			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.TEXT_PLAIN)
					.body("Password reset successful");
		} catch (InvalidVerificationTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.TEXT_PLAIN)
					.body(null);
		}
	}
}
