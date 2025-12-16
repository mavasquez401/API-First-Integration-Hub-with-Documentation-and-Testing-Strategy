package com.fidelity.integration.hub.adapter.impl;

import com.fidelity.integration.hub.adapter.MarketDataVendorAdapter;
import com.fidelity.integration.hub.adapter.domain.Instrument;
import com.fidelity.integration.hub.model.enums.AssetClass;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Simulated implementation of the market data vendor adapter for demonstration purposes.
 * 
 * In production, this would be replaced with a real implementation that:
 * - Makes HTTP calls to vendor APIs (e.g., Bloomberg, Reuters, Yahoo Finance, etc.)
 * - Handles API authentication (API keys, OAuth tokens)
 * - Implements rate limiting and quota management
 * - Caches market data to reduce vendor costs
 * - Handles errors and implements fallback mechanisms
 */
@Component
public class SimulatedMarketDataVendorAdapter implements MarketDataVendorAdapter {

    // Simulated market data cache
    private static final Map<String, BigDecimal> PRICE_CACHE = new HashMap<>();
    private static final Map<String, Instrument> INSTRUMENT_CACHE = new HashMap<>();

    static {
        // Initialize mock market data
        PRICE_CACHE.put("AAPL", new BigDecimal("175.25"));
        PRICE_CACHE.put("MSFT", new BigDecimal("380.50"));
        PRICE_CACHE.put("GOOGL", new BigDecimal("140.75"));
        PRICE_CACHE.put("TSLA", new BigDecimal("250.00"));
        PRICE_CACHE.put("AMZN", new BigDecimal("145.30"));

        INSTRUMENT_CACHE.put("AAPL", Instrument.builder()
            .symbol("AAPL")
            .name("Apple Inc.")
            .assetClass(AssetClass.EQUITY)
            .exchange("NASDAQ")
            .currentPrice(new BigDecimal("175.25"))
            .currency("USD")
            .securityId("037833100")
            .sector("Technology")
            .industry("Consumer Electronics")
            .lastUpdated(LocalDate.now())
            .build());

        INSTRUMENT_CACHE.put("MSFT", Instrument.builder()
            .symbol("MSFT")
            .name("Microsoft Corporation")
            .assetClass(AssetClass.EQUITY)
            .exchange("NASDAQ")
            .currentPrice(new BigDecimal("380.50"))
            .currency("USD")
            .securityId("594918104")
            .sector("Technology")
            .industry("Software")
            .lastUpdated(LocalDate.now())
            .build());

        INSTRUMENT_CACHE.put("GOOGL", Instrument.builder()
            .symbol("GOOGL")
            .name("Alphabet Inc.")
            .assetClass(AssetClass.EQUITY)
            .exchange("NASDAQ")
            .currentPrice(new BigDecimal("140.75"))
            .currency("USD")
            .securityId("02079K305")
            .sector("Technology")
            .industry("Internet Content & Information")
            .lastUpdated(LocalDate.now())
            .build());
    }

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        // Simulate vendor API call - in production, this would be an HTTP call
        BigDecimal price = PRICE_CACHE.get(symbol.toUpperCase());
        if (price == null) {
            // Default to a mock price if not found
            return new BigDecimal("100.00");
        }
        return price;
    }

    @Override
    public Instrument getInstrumentBySymbol(String symbol) {
        // Simulate vendor API call - in production, this would be an HTTP call
        Instrument instrument = INSTRUMENT_CACHE.get(symbol.toUpperCase());
        if (instrument == null) {
            // Return null for unknown instruments - service layer will handle not found
            return null;
        }
        return instrument;
    }
}
