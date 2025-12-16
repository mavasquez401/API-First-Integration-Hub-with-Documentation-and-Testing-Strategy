package com.fidelity.integration.hub.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to handle correlation IDs for request tracing.
 * 
 * Extracts correlation ID from request header (X-Correlation-ID) or generates a new one.
 * The correlation ID is added to:
 * - MDC (Mapped Diagnostic Context) for logging
 * - Response header for client tracing
 * 
 * This enables end-to-end request tracing across distributed systems.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Value("${integration.hub.correlation.header-name:X-Correlation-ID}")
    private String correlationHeaderName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract or generate correlation ID
        String correlationId = request.getHeader(correlationHeaderName);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Add to MDC for logging
        MDC.put("correlationId", correlationId);
        
        // Add to response header
        response.setHeader(correlationHeaderName, correlationId);
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC after request
            MDC.clear();
        }
    }
}
