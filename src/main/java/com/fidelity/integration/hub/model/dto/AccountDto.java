package com.fidelity.integration.hub.model.dto;

import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Account data transfer object.
 * Represents account information returned by the API.
 */
@Schema(description = "Account information")
public record AccountDto(
    @Schema(description = "Unique account identifier", example = "ACC-12345")
    String accountId,
    
    @Schema(description = "Client identifier who owns this account", example = "CLIENT-98765")
    String clientId,
    
    @Schema(description = "Account type", example = "BROKERAGE")
    AccountType accountType,
    
    @Schema(description = "Account status", example = "ACTIVE")
    AccountStatus status,
    
    @Schema(description = "Account display name", example = "My Investment Account")
    String displayName,
    
    @Schema(description = "Account number (masked)", example = "****1234")
    String accountNumber,
    
    @Schema(description = "Current account value", example = "125000.50")
    BigDecimal currentValue,
    
    @Schema(description = "Currency code", example = "USD")
    String currency,
    
    @Schema(description = "Account opened date")
    Instant openedDate,
    
    @Schema(description = "Last updated timestamp")
    Instant lastUpdated
) {}
