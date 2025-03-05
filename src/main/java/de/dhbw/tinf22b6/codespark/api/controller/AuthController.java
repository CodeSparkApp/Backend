package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RefreshTokenRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(@Autowired AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest request) {
		TokenResponse response = authService.loginAccount(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
		TokenResponse response = authService.refreshAccessToken(request);
		return response != null
				? ResponseEntity.ok().body(response)
				: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}
