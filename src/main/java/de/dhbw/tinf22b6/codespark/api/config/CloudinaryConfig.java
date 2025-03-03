package de.dhbw.tinf22b6.codespark.api.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CloudinaryConfig {
	private final Environment env;

	public CloudinaryConfig(@Autowired Environment env) {
		this.env = env;
	}

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(env.getRequiredProperty("cloudinary.api_url"));
	}
}
