package com.fidelity.integration.hub.exception;

import com.fidelity.integration.hub.model.dto.ErrorResponseDto;
import com.fidelity.integration.hub.model.dto.ViolationDto;
import com.fidelity.integration.hub.model.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that converts exceptions to RFC7807 Problem Details format.
 * 
 * This handler ensures consistent error responses across all API endpoints,
 * following the RFC7807 standard for problem details in HTTP APIs.
 * 
 * All errors include:
 * - type: URI identifying the problem type
 * - title: Human-readable summary
 * - status: HTTP status code
 * - detail: Specific explanation
 * - instance: URI identifying the specific occurrence
 * - correlationId: Request correlation ID for tracing
 * - errorCode: Application-specific error code
 * - timestamp: When the error occurred
 * - violations: Field-level validation errors (if applicable)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_TYPE_BASE_URI = "https://api.fidelity.com/problems/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.NOT_FOUND,
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            request
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        List<ViolationDto> violations = ex.getBindingResult().getFieldErrors().stream()
            .map(this::mapFieldError)
            .collect(Collectors.toList());
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.VALIDATION_ERROR,
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            request,
            violations
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        List<ViolationDto> violations = ex.getConstraintViolations().stream()
            .map(this::mapConstraintViolation)
            .collect(Collectors.toList());
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.VALIDATION_ERROR,
            HttpStatus.BAD_REQUEST,
            "Validation failed",
            request,
            violations
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String detail = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
            ex.getValue(), ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.BAD_REQUEST,
            HttpStatus.BAD_REQUEST,
            detail,
            request
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String detail = String.format("Required parameter '%s' is missing", ex.getParameterName());
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.BAD_REQUEST,
            HttpStatus.BAD_REQUEST,
            detail,
            request
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.BAD_REQUEST,
            HttpStatus.BAD_REQUEST,
            "Malformed request body",
            request
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.UNAUTHORIZED,
            HttpStatus.UNAUTHORIZED,
            "Authentication required",
            request
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.FORBIDDEN,
            HttpStatus.FORBIDDEN,
            "Access denied",
            request
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(ProviderException.class)
    public ResponseEntity<ErrorResponseDto> handleProviderException(
            ProviderException ex, HttpServletRequest request) {
        
        logger.error("Provider error occurred", ex);
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.PROVIDER_ERROR,
            HttpStatus.SERVICE_UNAVAILABLE,
            "External provider error: " + ex.getMessage(),
            request
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponseDto errorResponse = buildErrorResponse(
            ErrorCode.INTERNAL_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            request
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }

    /**
     * Builds an error response DTO following RFC7807 format.
     */
    private ErrorResponseDto buildErrorResponse(
            ErrorCode errorCode,
            HttpStatus status,
            String detail,
            HttpServletRequest request) {
        return buildErrorResponse(errorCode, status, detail, request, null);
    }

    private ErrorResponseDto buildErrorResponse(
            ErrorCode errorCode,
            HttpStatus status,
            String detail,
            HttpServletRequest request,
            List<ViolationDto> violations) {
        
        String correlationId = MDC.get("correlationId");
        String instance = request.getRequestURI();
        
        return new ErrorResponseDto(
            PROBLEM_TYPE_BASE_URI + errorCode.getCode().toLowerCase().replace("_", "-"),
            status.getReasonPhrase(),
            status.value(),
            detail,
            instance,
            correlationId,
            errorCode.getCode(),
            Instant.now(),
            violations,
            null
        );
    }

    private ViolationDto mapFieldError(FieldError fieldError) {
        return new ViolationDto(
            fieldError.getField(),
            fieldError.getDefaultMessage(),
            fieldError.getRejectedValue()
        );
    }

    private ViolationDto mapConstraintViolation(ConstraintViolation<?> violation) {
        return new ViolationDto(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue()
        );
    }
}
