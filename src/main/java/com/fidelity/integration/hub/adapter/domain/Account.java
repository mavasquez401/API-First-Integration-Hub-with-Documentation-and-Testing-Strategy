package com.fidelity.integration.hub.adapter.domain;

import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain model representing an account from the OMS system.
 * This is the internal representation used between adapters and services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String accountId;
    private String clientId;
    private AccountType accountType;
    private AccountStatus status;
    private String displayName;
    private String accountNumber;
    private BigDecimal currentValue;
    private String currency;
    private Instant openedDate;
    private Instant lastUpdated;
}
