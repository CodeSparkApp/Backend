package de.dhbw.tinf22b6.codespark.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.payload.request.LoginRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RefreshTokenRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.TokenResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthControllerTests.TestConfig.class)
class AuthControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthService authService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@TestConfiguration
	static class TestConfig {
		@Bean
		public AuthService authService() {
			return Mockito.mock(AuthService.class);
		}
	}

	@Test
	void testLoginUser() throws Exception {
		LoginRequest request = new LoginRequest("user@example.com", "password123");
		TokenResponse mockResponse = new TokenResponse("access123", "refresh456");

		Mockito.when(authService.loginAccount(any(LoginRequest.class)))
				.thenReturn(mockResponse);

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.access_token").value("access123"))
				.andExpect(jsonPath("$.refresh_token").value("refresh456"));
	}

	@Test
	void testRefreshAccessToken() throws Exception {
		RefreshTokenRequest request = new RefreshTokenRequest("refresh456");
		TokenResponse mockResponse = new TokenResponse("access789", "refresh456");

		Mockito.when(authService.refreshAccessToken(any(RefreshTokenRequest.class)))
				.thenReturn(mockResponse);

		mockMvc.perform(post("/api/v1/auth/refresh")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.access_token").value("access789"))
				.andExpect(jsonPath("$.refresh_token").value("refresh456"));
	}
}
