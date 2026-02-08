package br.gov.sus.telemedicina;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "scheduler.enabled=false",
        "spring.flyway.enabled=false"
})
class TelemedicinaApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}

