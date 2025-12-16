package com.fidelity.integration.hub.adapter;

import com.fidelity.integration.hub.adapter.domain.Account;
import com.fidelity.integration.hub.adapter.domain.Position;

import java.util.List;

/**
 * Adapter interface for integrating with the internal Order Management System (OMS).
 * 
 * This adapter abstracts the OMS integration details and provides a clean interface
 * for the core services to consume account and position data.
 * 
 * In production, implementations would handle:
 * - HTTP/REST calls to OMS services
 * - Database queries to OMS databases
 * - Connection pooling and retry logic
 * - Error handling and timeout management
 */
public interface OmsAdapter {

    /**
     * Retrieves a single account by accountId.
     *
     * @param accountId Account identifier
     * @return Account if found, otherwise null
     */
    Account getAccountById(String accountId);

    /**
     * Retrieves all accounts for a given client.
     * 
     * @param clientId Client identifier
     * @return List of accounts
     */
    List<Account> getAccountsByClient(String clientId);

    /**
     * Retrieves all positions for a given account.
     * 
     * @param accountId Account identifier
     * @return List of positions
     */
    List<Position> getPositionsByAccount(String accountId);
}
