package com.fidelity.integration.hub.model.enums;

/**
 * Account type enumeration.
 * Represents the type of account (e.g., brokerage, retirement, trust).
 */
public enum AccountType {
    /** Individual brokerage account */
    BROKERAGE,
    
    /** Individual Retirement Account */
    IRA,
    
    /** 401(k) retirement account */
    RETIREMENT_401K,
    
    /** Trust account */
    TRUST,
    
    /** Joint account */
    JOINT,
    
    /** Corporate account */
    CORPORATE,
    
    /** Custodial account */
    CUSTODIAL
}
