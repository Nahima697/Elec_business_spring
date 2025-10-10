package com.elec_business.config;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @Primary
    @ServiceConnection

        // Ensures fresh container for each test run during development
   static PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:17")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("init_postgres.sql");
    }

}