package com.fidelity.integration.hub.exception;

/**
 * Exception thrown when an external provider (OMS, market data vendor, etc.) encounters an error.
 */
public class ProviderException extends RuntimeException {
    
    public ProviderException(String message) {
        super(message);
    }
    
    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
