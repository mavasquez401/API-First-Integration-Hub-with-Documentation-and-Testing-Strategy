package com.fidelity.integration.hub.service;

import com.fidelity.integration.hub.adapter.OmsAdapter;
import com.fidelity.integration.hub.adapter.domain.Account;
import com.fidelity.integration.hub.exception.ResourceNotFoundException;
import com.fidelity.integration.hub.model.dto.AccountDto;
import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AccountService.
 * Tests business logic in isolation using mocked adapters.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private OmsAdapter omsAdapter;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount1;
    private Account testAccount2;

    @BeforeEach
    void setUp() {
        testAccount1 = Account.builder()
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
            .build();

        testAccount2 = Account.builder()
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
            .build();
    }

    @Test
    void getAccountsByClient_Success_ReturnsAllAccounts() {
        // Given
        String clientId = "CLIENT-98765";
        when(omsAdapter.getAccountsByClient(clientId))
            .thenReturn(Arrays.asList(testAccount1, testAccount2));

        // When
        List<AccountDto> result = accountService.getAccountsByClient(clientId, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ACC-12345", result.get(0).accountId());
        assertEquals("ACC-12346", result.get(1).accountId());
    }

    @Test
    void getAccountsByClient_WithStatusFilter_FiltersByStatus() {
        // Given
        String clientId = "CLIENT-98765";
        when(omsAdapter.getAccountsByClient(clientId))
            .thenReturn(Arrays.asList(testAccount1, testAccount2));

        // When
        List<AccountDto> result = accountService.getAccountsByClient(clientId, AccountStatus.ACTIVE, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(account -> account.status() == AccountStatus.ACTIVE));
    }

    @Test
    void getAccountsByClient_WithTypeFilter_FiltersByType() {
        // Given
        String clientId = "CLIENT-98765";
        when(omsAdapter.getAccountsByClient(clientId))
            .thenReturn(Arrays.asList(testAccount1, testAccount2));

        // When
        List<AccountDto> result = accountService.getAccountsByClient(clientId, null, AccountType.BROKERAGE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AccountType.BROKERAGE, result.get(0).accountType());
    }

    @Test
    void getAccountsByClient_ClientNotFound_ThrowsException() {
        // Given
        String clientId = "CLIENT-NOTFOUND";
        when(omsAdapter.getAccountsByClient(clientId))
            .thenReturn(Collections.emptyList());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountsByClient(clientId, null, null);
        });
    }
}
