package de.dhbw.tinf22b6.codespark.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.tinf22b6.codespark.api.payload.request.PromptRequest;
import de.dhbw.tinf22b6.codespark.api.security.JwtFilter;
import de.dhbw.tinf22b6.codespark.api.service.interfaces.ConversationService;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConversationController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(ConversationControllerTests.TestConfig.class)
class ConversationControllerTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ConversationService conversationService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@TestConfiguration
	static class TestConfig {
		@Bean
		public ConversationService conversationService() {
			return Mockito.mock(ConversationService.class);
		}
	}

	@Test
	void testProcessPrompt() throws Exception {
		PromptRequest request = new PromptRequest("What is Java?");
		String mockResponse = "Java is a programming language.";

		Mockito.when(conversationService.processPrompt(any(), any(PromptRequest.class)))
				.thenReturn(mockResponse);

		mockMvc.perform(post("/api/v1/openai/prompt")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().string(mockResponse));
	}

	@Test
	void testProcessPromptStream() throws Exception {
		PromptRequest request = new PromptRequest("What is Java?");
		String expectedStreamedText = "Java is a programming language.";

		StreamingResponseBody responseBody = stream -> stream.write(expectedStreamedText.getBytes());

		Mockito.when(conversationService.processPromptStream(any(), any(PromptRequest.class)))
				.thenReturn(responseBody);

		mockMvc.perform(post("/api/v1/openai/prompt-stream")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().string(expectedStreamedText));
	}
}
