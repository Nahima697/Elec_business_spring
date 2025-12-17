package com.elec_business;

import com.elec_business.config.TestcontainersConfiguration; // Ajoute l'import
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import; // Ajoute l'import

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(TestcontainersConfiguration.class) 
class ElecBusinessApplicationTests {

    @Test
    void contextLoads() {
    }
}
