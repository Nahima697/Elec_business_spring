package com.elec_business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.elec_business.repository")
public class ElecBusinessApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElecBusinessApplication.class, args);
	}

}
