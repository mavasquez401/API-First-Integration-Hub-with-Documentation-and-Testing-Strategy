package com.fidelity.integration.hub.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check controller.
 * Provides liveness and readiness endpoints for monitoring and orchestration.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Health", description = "Health check and status endpoints")
public class HealthController {

    @Operation(
        summary = "Health check",
        description = "Returns the health status of the integration hub and its dependencies"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> health() {
        // In production, this would check actual dependency health
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "api-first-integration-hub",
            "version", "1.0.0",
            "checks", Map.of(
                "oms", "UP",
                "marketData", "UP"
            )
        ));
    }
}
