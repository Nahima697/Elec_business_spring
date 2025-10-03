package com.elec_business;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.elec_business")
@EnableScheduling
public class ElecBusinessApplication {
	@Value("${spring.datasource.url:NOT_FOUND}")
	private String dbUrl;

	@PostConstruct
	public void logDbUrl() {
		System.out.println("🔍 spring.datasource.url = " + dbUrl);
	}

	public static void main(String[] args) {
		SpringApplication.run(ElecBusinessApplication.class, args);
	}

}
