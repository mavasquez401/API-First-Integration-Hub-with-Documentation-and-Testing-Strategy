package com.fidelity.integration.hub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Error response DTO following RFC7807 Problem Details format.
 * Provides structured error information for API consumers.
 */
@Schema(description = "Error response following RFC7807 Problem Details format")
public record ErrorResponseDto(
    @Schema(description = "A URI reference that identifies the problem type", example = "https://api.fidelity.com/problems/validation-error")
    String type,
    
    @Schema(description = "A short, human-readable summary of the problem type", example = "Validation Error")
    String title,
    
    @Schema(description = "The HTTP status code", example = "400")
    Integer status,
    
    @Schema(description = "A human-readable explanation specific to this occurrence of the problem", example = "Account status filter is invalid")
    String detail,
    
    @Schema(description = "A URI reference that identifies the specific occurrence of the problem", example = "/api/v1/clients/CLIENT-123/accounts")
    String instance,
    
    @Schema(description = "Correlation ID for tracing the request", example = "abc-123-def-456")
    String correlationId,
    
    @Schema(description = "Application-specific error code", example = "VALIDATION_ERROR")
    String errorCode,
    
    @Schema(description = "Timestamp when the error occurred")
    Instant timestamp,
    
    @Schema(description = "Field-level validation errors")
    List<ViolationDto> violations,
    
    @Schema(description = "Additional metadata about the error")
    Map<String, Object> metadata
) {}

/**
 * Violation DTO for field-level validation errors.
 */
@Schema(description = "Field-level validation violation")
public record ViolationDto(
    @Schema(description = "Field name that failed validation", example = "accountStatus")
    String field,
    
    @Schema(description = "Error message", example = "must be one of [ACTIVE, CLOSED, PENDING, SUSPENDED, DORMANT]")
    String message,
    
    @Schema(description = "Rejected value", example = "INVALID_STATUS")
    Object rejectedValue
) {}
