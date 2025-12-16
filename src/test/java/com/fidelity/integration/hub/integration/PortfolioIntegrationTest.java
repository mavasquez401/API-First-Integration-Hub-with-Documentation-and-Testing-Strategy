package com.fidelity.integration.hub.integration;

import com.fidelity.integration.hub.model.dto.PortfolioDto;
import com.fidelity.integration.hub.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for PortfolioService.
 * 
 * This test uses the actual Spring context with simulated adapters.
 * In production, you would replace SimulatedOmsAdapter and SimulatedMarketDataVendorAdapter
 * with real implementations or use Testcontainers/WireMock for integration testing.
 * 
 * Note: For full integration tests with external services, consider:
 * - WireMock for HTTP service stubbing
 * - Testcontainers for database integration
 * - Contract testing with Pact or Spring Cloud Contract
 */
@SpringBootTest
@ActiveProfiles("test")
class PortfolioIntegrationTest {

    @Autowired
    private PortfolioService portfolioService;

    @Test
    void getPortfolioByAccount_WithValidAccount_ReturnsPortfolio() {
        // Given
        String accountId = "ACC-12345";

        // When
        PortfolioDto portfolio = portfolioService.getPortfolioByAccount(accountId);

        // Then
        assertNotNull(portfolio);
        assertEquals(accountId, portfolio.accountId());
        assertNotNull(portfolio.positions());
        assertTrue(portfolio.totalValue().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(portfolio.currency());
    }
}
