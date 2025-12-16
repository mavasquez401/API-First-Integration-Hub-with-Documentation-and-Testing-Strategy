package com.fidelity.integration.hub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Violation DTO for field-level validation errors.
 *
 * This is separated into its own file because Java allows only one public top-level type per file.
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


