package de.dhbw.tinf22b6.codespark.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	@Bean(name = "BadgeExecutor")
	public Executor badgeExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("BadgeExecutor-");
		executor.initialize();
		return executor;
	}

	@Bean(name = "DefaultExecutor")
	public Executor defaultExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(6);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("DefaultExecutor-");
		executor.initialize();
		return executor;
	}

	@Override
	public Executor getAsyncExecutor() {
		return defaultExecutor();
	}
}
