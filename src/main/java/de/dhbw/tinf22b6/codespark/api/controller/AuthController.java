package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.exception.InvalidAccountCredentialsException;
import de.dhbw.tinf22b6.codespark.api.exception.UnverifiedAccountException;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RefreshTokenRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtUtil;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthController {
	private final JwtUtil jwtUtil;
	private final AccountService accountService;

	public AuthController(@Autowired JwtUtil jwtUtil,
						  @Autowired AccountService accountService) {
		this.jwtUtil = jwtUtil;
		this.accountService = accountService;
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
		if (jwtUtil.validateToken(request.getRefreshToken())) {
			String username = jwtUtil.extractUsername(request.getRefreshToken());
			String role = jwtUtil.extractRole(request.getRefreshToken());
			String newAccessToken = jwtUtil.generateAccessToken(username, role);
			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(new TokenResponse(newAccessToken, request.getRefreshToken()));
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
		try {
			TokenResponse response = accountService.loginAccount(request);
			return ResponseEntity.status(HttpStatus.OK)
					.contentType(MediaType.APPLICATION_JSON)
					.body(response);
		} catch (InvalidAccountCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.TEXT_PLAIN)
					.body(null);
		} catch (UnverifiedAccountException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.contentType(MediaType.TEXT_PLAIN)
					.body(null);
		}
	}
}
