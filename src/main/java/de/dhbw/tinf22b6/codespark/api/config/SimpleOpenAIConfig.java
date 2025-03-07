package de.dhbw.tinf22b6.codespark.api.config;

import io.github.sashirestela.openai.SimpleOpenAI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SimpleOpenAIConfig {
	private final Environment env;

	public SimpleOpenAIConfig(@Autowired Environment env) {
		this.env = env;
	}

	@Bean
	public SimpleOpenAI simpleOpenAI() {
		return SimpleOpenAI.builder()
				.apiKey(env.getRequiredProperty("openai.api.key"))
				.organizationId(env.getRequiredProperty("openai.api.organization-id"))
				.projectId(env.getRequiredProperty("openai.api.project-id"))
				.build();
	}
}
