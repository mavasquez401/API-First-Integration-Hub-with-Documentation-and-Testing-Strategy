package com.fidelity.integration.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point for the API-First Integration Hub.
 * 
 * This hub provides REST APIs for portfolio and account aggregation,
 * integrating with internal OMS systems and external market data vendors.
 */
@SpringBootApplication
public class IntegrationHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationHubApplication.class, args);
    }
}
