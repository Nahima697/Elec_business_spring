package com.elec_business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.elec_business")
@EnableScheduling
public class ElecBusinessApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElecBusinessApplication.class, args);
	}

}
