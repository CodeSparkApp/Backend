package de.dhbw.tinf22b6.codespark.api.service.interfaces;

import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RefreshTokenRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;

public interface AuthService {
	TokenResponse loginAccount(LoginRequest request);
	TokenResponse refreshAccessToken(RefreshTokenRequest request);
}
