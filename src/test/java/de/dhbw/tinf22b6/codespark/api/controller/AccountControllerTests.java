package de.dhbw.tinf22b6.codespark.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.payload.request.AccountCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.PasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.RequestPasswordResetRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.AccountDetailsResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.UploadImageResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.AccountService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class,
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(AccountControllerTests.TestConfig.class)
class AccountControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountService accountService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@TestConfiguration
	static class TestConfig {
		@Bean
		public AccountService accountService() {
			return Mockito.mock(AccountService.class);
		}
	}

	@Test
	@WithMockUser
	void testGetAccountDetails() throws Exception {
		AccountDetailsResponse mockResponse = new AccountDetailsResponse(
				UUID.fromString("e6c9f025-33ea-4b92-a114-6b6ee6c47d36"),
				"user",
				"email@example.com",
				"https://photos.com/user-profile-photo",
				LocalDateTime.of(2022, 1, 1, 12, 0, 0)
		);
		Mockito.when(accountService.getAccountDetails(any())).thenReturn(mockResponse);

		mockMvc.perform(get("/api/v1/account/profile"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("e6c9f025-33ea-4b92-a114-6b6ee6c47d36"))
				.andExpect(jsonPath("$.username").value("user"))
				.andExpect(jsonPath("$.email").value("email@example.com"))
				.andExpect(jsonPath("$.profile_image_url").value("https://photos.com/user-profile-photo"))
				.andExpect(jsonPath("$.creation_date").value("2022-01-01T12:00:00"));
	}

	@Test
	void testRegisterAccount() throws Exception {
		AccountCreateRequest request = new AccountCreateRequest(
				"newuser",
				"newuser@example.com",
				"password123"
		);

		mockMvc.perform(post("/api/v1/account/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());

		Mockito.verify(accountService).createAccount(any(AccountCreateRequest.class));
	}

	@Test
	void testVerifyEmail() throws Exception {
		mockMvc.perform(get("/api/v1/account/verify")
						.param("token", "44f8622d-cea4-4027-a949-da79cad454ed"))
				.andExpect(status().isOk());

		Mockito.verify(accountService).verifyEmail("44f8622d-cea4-4027-a949-da79cad454ed");
	}

	@Test
	void testRequestPasswordReset() throws Exception {
		RequestPasswordResetRequest request = new RequestPasswordResetRequest("user@example.com");

		mockMvc.perform(post("/api/v1/account/request-reset")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());

		Mockito.verify(accountService).requestPasswordReset(any(RequestPasswordResetRequest.class));
	}

	@Test
	void testResetPassword() throws Exception {
		PasswordResetRequest request = new PasswordResetRequest("d2a8c4aa-a604-4506-917e-345e30f8e50a", "password123");

		mockMvc.perform(post("/api/v1/account/reset-password")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());

		Mockito.verify(accountService).resetPassword(any(PasswordResetRequest.class));
	}

	@Test
	@WithMockUser
	void testUpdateProfileImage() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "imagecontent".getBytes());
		UploadImageResponse mockResponse = new UploadImageResponse("https://example.com/image.jpg");

		Mockito.when(accountService.updateProfileImage(any(), any())).thenReturn(mockResponse);

		mockMvc.perform(multipart("/api/v1/account/upload-profile-image")
						.file(file))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.image_url").value("https://example.com/image.jpg"));
	}
}
