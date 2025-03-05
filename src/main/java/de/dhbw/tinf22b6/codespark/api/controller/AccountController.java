package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/account")
public class AccountController {
	private final AccountService accountService;

	public AccountController(@Autowired AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/register")
	public ResponseEntity<Void> registerAccount(@RequestBody AccountCreateRequest request) {
		accountService.createAccount(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/verify")
	public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
		accountService.verifyEmail(token);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/request-reset")
	public ResponseEntity<Void> requestPasswordReset(@RequestBody String email) {
		accountService.requestPasswordReset(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetRequest request) {
		accountService.resetPassword(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/upload-profile-image")
	public ResponseEntity<UploadImageResponse> updateProfileImage(@RequestParam("file") MultipartFile file, Principal principal) {
		String username = principal.getName();
		UUID accountId = accountService.getAccountIdByUsername(username);

		UploadImageResponse response = accountService.updateProfileImage(accountId, file);
		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(response);
	}
}
