package de.dhbw.tinf22b6.codespark.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.dhbw.tinf22b6.codespark.api.payload.request.ExamDateUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ExamDateResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ExamDateService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExamDateController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(ExamDateControllerTests.TestConfig.class)
class ExamDateControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ExamDateService examDateService;

	private final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ExamDateService examDateService() {
			return Mockito.mock(ExamDateService.class);
		}
	}

	@Test
	void testGetExamDate() throws Exception {
		ExamDateResponse response = new ExamDateResponse(LocalDateTime.of(2025, 7, 15, 3, 0, 0));
		Mockito.when(examDateService.getExamDate(any())).thenReturn(response);

		mockMvc.perform(get("/api/v1/exam-date/get"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.exam_date").value("2025-07-15T03:00:00"));
	}

	@Test
	void testUpdateExamDate() throws Exception {
		ExamDateUpdateRequest request = new ExamDateUpdateRequest(LocalDateTime.of(2025, 6, 12, 9, 0, 0));

		mockMvc.perform(post("/api/v1/exam-date/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk());

		Mockito.verify(examDateService).updateExamDate(any(ExamDateUpdateRequest.class), any());
	}
}
