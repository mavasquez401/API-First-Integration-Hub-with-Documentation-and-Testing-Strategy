package com.fidelity.integration.hub.adapter.impl;

import com.fidelity.integration.hub.adapter.OmsAdapter;
import com.fidelity.integration.hub.adapter.domain.Account;
import com.fidelity.integration.hub.adapter.domain.Position;
import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import com.fidelity.integration.hub.model.enums.AssetClass;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simulated implementation of the OMS adapter for demonstration purposes.
 * 
 * In production, this would be replaced with a real implementation that:
 * - Makes HTTP calls to OMS REST APIs
 * - Queries OMS databases directly
 * - Handles connection pooling, timeouts, and retries
 * - Implements proper error handling and logging
 */
@Component
public class SimulatedOmsAdapter implements OmsAdapter {

    // Simulated in-memory data store
    private static final List<Account> MOCK_ACCOUNTS = new ArrayList<>();
    private static final List<Position> MOCK_POSITIONS = new ArrayList<>();

    static {
        // Initialize mock data
        MOCK_ACCOUNTS.add(Account.builder()
            .accountId("ACC-12345")
            .clientId("CLIENT-98765")
            .accountType(AccountType.BROKERAGE)
            .status(AccountStatus.ACTIVE)
            .displayName("My Investment Account")
            .accountNumber("****1234")
            .currentValue(new BigDecimal("125000.50"))
            .currency("USD")
            .openedDate(Instant.parse("2020-01-15T00:00:00Z"))
            .lastUpdated(Instant.now())
            .build());

        MOCK_ACCOUNTS.add(Account.builder()
            .accountId("ACC-12346")
            .clientId("CLIENT-98765")
            .accountType(AccountType.IRA)
            .status(AccountStatus.ACTIVE)
            .displayName("My Retirement Account")
            .accountNumber("****5678")
            .currentValue(new BigDecimal("250000.00"))
            .currency("USD")
            .openedDate(Instant.parse("2018-06-20T00:00:00Z"))
            .lastUpdated(Instant.now())
            .build());

        // Mock positions for ACC-12345
        MOCK_POSITIONS.add(Position.builder()
            .symbol("AAPL")
            .instrumentName("Apple Inc.")
            .assetClass(AssetClass.EQUITY)
            .quantity(new BigDecimal("100"))
            .costBasisPerShare(new BigDecimal("150.00"))
            .currency("USD")
            .build());

        MOCK_POSITIONS.add(Position.builder()
            .symbol("MSFT")
            .instrumentName("Microsoft Corporation")
            .assetClass(AssetClass.EQUITY)
            .quantity(new BigDecimal("50"))
            .costBasisPerShare(new BigDecimal("200.00"))
            .currency("USD")
            .build());

        MOCK_POSITIONS.add(Position.builder()
            .symbol("GOOGL")
            .instrumentName("Alphabet Inc.")
            .assetClass(AssetClass.EQUITY)
            .quantity(new BigDecimal("25"))
            .costBasisPerShare(new BigDecimal("100.00"))
            .currency("USD")
            .build());
    }

    @Override
    public List<Account> getAccountsByClient(String clientId) {
        // Simulate OMS lookup - in production, this would be an HTTP/DB call
        return MOCK_ACCOUNTS.stream()
            .filter(account -> account.getClientId().equals(clientId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Position> getPositionsByAccount(String accountId) {
        // Simulate OMS lookup - in production, this would be an HTTP/DB call
        // For demo purposes, return positions for ACC-12345
        if ("ACC-12345".equals(accountId)) {
            return new ArrayList<>(MOCK_POSITIONS);
        }
        return new ArrayList<>();
    }
}
