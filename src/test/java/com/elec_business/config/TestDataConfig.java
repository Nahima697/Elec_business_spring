package com.elec_business.config;


import com.elec_business.data.TestDataLoader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@RequiredArgsConstructor
public class TestDataConfig {

    private final TestDataLoader testDataLoader;

    @PostConstruct
    public void preloadTestData() {
        testDataLoader.load();
    }
}
