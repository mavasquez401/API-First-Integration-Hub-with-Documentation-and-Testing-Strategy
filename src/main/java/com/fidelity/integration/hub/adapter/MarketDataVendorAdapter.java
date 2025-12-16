package com.fidelity.integration.hub.adapter;

import com.fidelity.integration.hub.adapter.domain.Instrument;

import java.math.BigDecimal;

/**
 * Adapter interface for integrating with external market data vendors.
 * 
 * This adapter abstracts the vendor integration details and provides a clean interface
 * for retrieving market data such as current prices and instrument metadata.
 * 
 * In production, implementations would handle:
 * - HTTP/REST calls to vendor APIs
 * - Authentication/authorization (API keys, OAuth, etc.)
 * - Rate limiting and quota management
 * - Caching to reduce vendor calls
 * - Error handling and fallback mechanisms
 */
public interface MarketDataVendorAdapter {

    /**
     * Retrieves the current market price for an instrument.
     * 
     * @param symbol Instrument symbol/ticker
     * @return Current market price
     */
    BigDecimal getCurrentPrice(String symbol);

    /**
     * Retrieves complete instrument reference data by symbol.
     * 
     * @param symbol Instrument symbol/ticker
     * @return Instrument metadata, or null if not found
     */
    Instrument getInstrumentBySymbol(String symbol);
}
