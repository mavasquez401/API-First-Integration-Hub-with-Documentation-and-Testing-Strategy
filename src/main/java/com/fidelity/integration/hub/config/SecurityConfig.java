package com.fidelity.integration.hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Security configuration for OAuth2 JWT resource server.
 * 
 * This configuration assumes:
 * - OAuth2 JWT tokens are provided in the Authorization header (Bearer token)
 * - Token validation is performed by Spring Security OAuth2 Resource Server
 * - JWK Set URI is configured in application.yml (spring.security.oauth2.resourceserver.jwt.jwk-set-uri)
 * 
 * In production, ensure proper scopes and roles are enforced on endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security toggle to make local/dev usage easy while keeping a production-ready configuration available.
     *
     * When disabled:
     * - all API endpoints are permitted without JWT
     *
     * When enabled:
     * - /api/** requires authenticated JWT (OAuth2 Resource Server)
     */
    @Value("${integration.hub.security.enabled:false}")
    private boolean securityEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for a stateless API
        http.csrf(AbstractHttpConfigurer::disable);

        // Security headers (apply in both modes)
        http.headers(headers -> headers
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
        );

        if (!securityEnabled) {
            // Local/dev mode: allow everything (keeps the demo runnable without real IdP/JWKS).
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        // Production mode: OAuth2 Resource Server + stateless sessions.
        http
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/health", "/swagger-ui/**", "/api-docs/**", "/actuator/health").permitAll()
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
