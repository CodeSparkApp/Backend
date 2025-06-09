package de.dhbw.tinf22b6.codespark.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.common.LessonProgressState;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterCreateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.request.ChapterUpdateRequest;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.ChapterOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonItemResponse;
import de.dhbw.tinf22b6.codespark.api.payload.response.LessonOverviewResponse;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ChapterService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChapterController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(ChapterControllerTests.TestConfig.class)
class ChapterControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ChapterService chapterService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ChapterService chapterService() {
			return Mockito.mock(ChapterService.class);
		}
	}

	@Test
	void testGetChapterOverview() throws Exception {
		ChapterItemResponse chapterItem = new ChapterItemResponse(UUID.randomUUID(), "Intro", 0.5f);
		ChapterOverviewResponse response = new ChapterOverviewResponse(List.of(chapterItem));

		Mockito.when(chapterService.getChapterOverview(any())).thenReturn(response);

		mockMvc.perform(get("/api/v1/chapter/overview"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.chapters[0].title").value("Intro"))
				.andExpect(jsonPath("$.chapters[0].progress").value(0.5));
	}

	@Test
	void testGetLessonOverview() throws Exception {
		UUID chapterId = UUID.randomUUID();

		LessonItemResponse lesson = new LessonItemResponse(UUID.randomUUID(), "Basics", LessonProgressState.UNATTEMPTED);
		LessonOverviewResponse response = new LessonOverviewResponse("Chapter 1", "Description", List.of(lesson));

		Mockito.when(chapterService.getLessonOverview(eq(chapterId), any())).thenReturn(response);

		mockMvc.perform(get("/api/v1/chapter/" + chapterId + "/lessons"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.chapter_title").value("Chapter 1"))
				.andExpect(jsonPath("$.lessons[0].title").value("Basics"))
				.andExpect(jsonPath("$.lessons[0].state").value("UNATTEMPTED"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateChapter() throws Exception {
		ChapterCreateRequest request = new ChapterCreateRequest(
				"New Chapter", "Description",
				UUID.randomUUID(), UUID.randomUUID());

		mockMvc.perform(post("/api/v1/chapter/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		Mockito.verify(chapterService).createChapter(any(ChapterCreateRequest.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testUpdateChapter() throws Exception {
		UUID chapterId = UUID.randomUUID();
		ChapterUpdateRequest request = new ChapterUpdateRequest(UUID.randomUUID(), UUID.randomUUID());

		mockMvc.perform(post("/api/v1/chapter/" + chapterId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		Mockito.verify(chapterService).updateChapter(eq(chapterId), any(ChapterUpdateRequest.class));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void testDeleteChapter() throws Exception {
		UUID chapterId = UUID.randomUUID();

		mockMvc.perform(delete("/api/v1/chapter/" + chapterId))
				.andExpect(status().isOk());

		Mockito.verify(chapterService).deleteChapter(eq(chapterId));
	}
}
