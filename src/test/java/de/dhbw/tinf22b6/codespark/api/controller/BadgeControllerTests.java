package de.dhbw.tinf22b6.codespark.api.controller;

import de.dhbw.tinf22b6.codespark.api.payload.response.BadgeItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.BadgesOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.BadgeService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BadgeController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(BadgeControllerTests.TestConfig.class)
class BadgeControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BadgeService badgeService;

	@TestConfiguration
	static class TestConfig {
		@Bean
		public BadgeService badgeService() {
			return Mockito.mock(BadgeService.class);
		}
	}

	@Test
	void testGetBadgesOverview() throws Exception {
		BadgeItemResponse badge = new BadgeItemResponse(
				UUID.fromString("c6ba0917-e3b3-48d7-af4f-5dc63e282308"),
				"Completionist",
				"Complete all challenges",
				"trophy.png",
				LocalDateTime.parse("2007-12-03T10:15:30")
		);

		BadgesOverviewResponse response = new BadgesOverviewResponse(List.of(badge));

		Mockito.when(badgeService.getBadgesOverview(any()))
				.thenReturn(response);

		mockMvc.perform(get("/api/v1/badge/overview"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.badges").isArray())
				.andExpect(jsonPath("$.badges[0].id").value("c6ba0917-e3b3-48d7-af4f-5dc63e282308"))
				.andExpect(jsonPath("$.badges[0].name").value("Completionist"))
				.andExpect(jsonPath("$.badges[0].description").value("Complete all challenges"))
				.andExpect(jsonPath("$.badges[0].icon").value("trophy.png"))
				.andExpect(jsonPath("$.badges[0].receive_date").value("2007-12-03T10:15:30"));
	}
}
