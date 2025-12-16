package com.fidelity.integration.hub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test to verify Spring Boot application context loads successfully.
 * This is a basic smoke test to ensure all beans are wired correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
class IntegrationHubApplicationTests {

    @Test
    void contextLoads() {
        // If the context loads without exceptions, the test passes
    }
}
