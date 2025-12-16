package com.fidelity.integration.hub.service;

import com.fidelity.integration.hub.model.dto.AccountDto;
import com.fidelity.integration.hub.model.enums.AccountStatus;
import com.fidelity.integration.hub.model.enums.AccountType;
import com.fidelity.integration.hub.adapter.OmsAdapter;
import com.fidelity.integration.hub.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for account-related business logic.
 * Orchestrates calls to the OMS adapter and transforms domain models to DTOs.
 */
@Service
public class AccountService {

    private final OmsAdapter omsAdapter;

    public AccountService(OmsAdapter omsAdapter) {
        this.omsAdapter = omsAdapter;
    }

    /**
     * Retrieves accounts for a client with optional filtering.
     * 
     * @param clientId Client identifier
     * @param accountStatus Optional status filter
     * @param accountType Optional type filter
     * @return List of account DTOs
     */
    public List<AccountDto> getAccountsByClient(String clientId, AccountStatus accountStatus, AccountType accountType) {
        // Fetch accounts from OMS adapter
        List<com.fidelity.integration.hub.adapter.domain.Account> accounts = omsAdapter.getAccountsByClient(clientId);
        
        if (accounts.isEmpty()) {
            throw new ResourceNotFoundException("Client not found: " + clientId);
        }
        
        // Apply filters if provided
        List<AccountDto> filteredAccounts = accounts.stream()
            .filter(account -> accountStatus == null || account.getStatus() == accountStatus)
            .filter(account -> accountType == null || account.getAccountType() == accountType)
            .map(this::toDto)
            .collect(Collectors.toList());
        
        // If filters resulted in empty list, still return empty list (not an error)
        return filteredAccounts;
    }

    /**
     * Converts domain Account model to DTO.
     */
    private AccountDto toDto(com.fidelity.integration.hub.adapter.domain.Account account) {
        return new AccountDto(
            account.getAccountId(),
            account.getClientId(),
            account.getAccountType(),
            account.getStatus(),
            account.getDisplayName(),
            account.getAccountNumber(),
            account.getCurrentValue(),
            account.getCurrency(),
            account.getOpenedDate(),
            account.getLastUpdated()
        );
    }
}
