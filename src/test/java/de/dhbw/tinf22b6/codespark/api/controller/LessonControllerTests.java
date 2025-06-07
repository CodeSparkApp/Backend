package de.dhbw.tinf22b6.codespark.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.dhbw.tinf22b6.codespark.api.common.LessonEvaluationState;
import de.dhbw.tinf22b6.codespark.api.common.LessonType;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.LessonUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.TheoryLessonSubmitRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonSubmitResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.TheoryLessonResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.BadgeService;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.LessonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LessonController.class,
		excludeFilters = @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(LessonControllerTests.TestConfig.class)
class LessonControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LessonService lessonService;

	@Autowired
	private BadgeService badgeService;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());

	@TestConfiguration
	static class TestConfig {
		@Bean
		public LessonService lessonService() {
			return Mockito.mock(LessonService.class);
		}

		@Bean
		public BadgeService badgeService() {
			return Mockito.mock(BadgeService.class);
		}
	}

	@Test
	void testGetLessonById() throws Exception {
		UUID lessonId = UUID.randomUUID();
		LessonResponse response = new TheoryLessonResponse();
		response.setId(lessonId);
		response.setTitle("Intro");

		Mockito.when(lessonService.getLessonById(eq(lessonId))).thenReturn(response);

		mockMvc.perform(get("/api/v1/lesson/" + lessonId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Intro"));
	}

	@Test
	void testEvaluateLesson() throws Exception {
		UUID lessonId = UUID.randomUUID();
		TheoryLessonSubmitRequest request = new TheoryLessonSubmitRequest();
		request.setType(LessonType.THEORY);

		LessonSubmitResponse response = new LessonSubmitResponse(
				LessonEvaluationState.CORRECT,
				"Good job",
				UUID.randomUUID()
		);

		Mockito.when(lessonService.evaluateLesson(eq(lessonId), any(), any()))
				.thenReturn(response);

		mockMvc.perform(post("/api/v1/lesson/" + lessonId + "/submit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.evaluation_result").value("CORRECT"))
				.andExpect(jsonPath("$.explanation").value("Good job"));

		Mockito.verify(badgeService).checkAndAssignBadges(any());
	}

	@Test
	void testSkipLesson() throws Exception {
		UUID lessonId = UUID.randomUUID();
		LessonSubmitResponse response = new LessonSubmitResponse();

		Mockito.when(lessonService.skipLesson(eq(lessonId), any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/lesson/" + lessonId + "/skip"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateLesson() throws Exception {
		LessonCreateRequest request = new LessonCreateRequest();

		mockMvc.perform(post("/api/v1/lesson/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		Mockito.verify(lessonService).createLesson(any(LessonCreateRequest.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateLesson() throws Exception {
		UUID lessonId = UUID.randomUUID();
		LessonUpdateRequest request = new LessonUpdateRequest();

		mockMvc.perform(post("/api/v1/lesson/" + lessonId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		Mockito.verify(lessonService).updateLesson(eq(lessonId), any(LessonUpdateRequest.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteLesson() throws Exception {
		UUID lessonId = UUID.randomUUID();

		mockMvc.perform(delete("/api/v1/lesson/" + lessonId))
				.andExpect(status().isOk());

		Mockito.verify(lessonService).deleteLesson(eq(lessonId));
	}
}
