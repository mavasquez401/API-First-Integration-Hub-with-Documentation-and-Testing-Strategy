package com.fidelity.integration.hub.model.enums;

/**
 * Error code enumeration.
 * Provides standardized error codes for API error responses.
 * These codes help consumers identify and handle specific error conditions.
 */
public enum ErrorCode {
    /** Resource not found */
    NOT_FOUND("RESOURCE_NOT_FOUND"),
    
    /** Validation error */
    VALIDATION_ERROR("VALIDATION_ERROR"),
    
    /** Unauthorized access */
    UNAUTHORIZED("UNAUTHORIZED"),
    
    /** Forbidden access */
    FORBIDDEN("FORBIDDEN"),
    
    /** Internal server error */
    INTERNAL_ERROR("INTERNAL_ERROR"),
    
    /** Service unavailable */
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
    
    /** External provider error */
    PROVIDER_ERROR("PROVIDER_ERROR"),
    
    /** Rate limit exceeded */
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED"),
    
    /** Bad request */
    BAD_REQUEST("BAD_REQUEST");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
