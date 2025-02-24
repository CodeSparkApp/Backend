package de.dhbw.tinf22b6.codespark.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
	private final Environment env;

	public MailConfig(@Autowired Environment env) {
		this.env = env;
	}

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(env.getRequiredProperty("smtp.host"));
		mailSender.setPort(env.getRequiredProperty("smtp.port", Integer.class));
		mailSender.setUsername(env.getRequiredProperty("smtp.email"));
		mailSender.setPassword(env.getRequiredProperty("smtp.password"));

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		return mailSender;
	}
}
