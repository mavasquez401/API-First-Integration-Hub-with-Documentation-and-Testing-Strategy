package com.fidelity.integration.hub.model.enums;

/**
 * Account status enumeration.
 * Represents the operational state of an account in the system.
 */
public enum AccountStatus {
    /** Account is active and available for transactions */
    ACTIVE,
    
    /** Account is closed and no longer active */
    CLOSED,
    
    /** Account is pending activation or approval */
    PENDING,
    
    /** Account is suspended or restricted */
    SUSPENDED,
    
    /** Account is in a dormant state */
    DORMANT
}
