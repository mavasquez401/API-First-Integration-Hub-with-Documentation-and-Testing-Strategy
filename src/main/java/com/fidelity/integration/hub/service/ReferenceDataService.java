package com.fidelity.integration.hub.service;

import com.fidelity.integration.hub.model.dto.InstrumentDto;
import com.fidelity.integration.hub.adapter.MarketDataVendorAdapter;
import com.fidelity.integration.hub.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for reference data operations.
 * Provides instrument metadata from market data vendors.
 */
@Service
public class ReferenceDataService {

    private final MarketDataVendorAdapter marketDataAdapter;

    public ReferenceDataService(MarketDataVendorAdapter marketDataAdapter) {
        this.marketDataAdapter = marketDataAdapter;
    }

    /**
     * Retrieves instrument reference data by symbol.
     * 
     * @param symbol Instrument symbol/ticker
     * @return Instrument DTO with metadata
     */
    public InstrumentDto getInstrumentBySymbol(String symbol) {
        com.fidelity.integration.hub.adapter.domain.Instrument instrument = marketDataAdapter.getInstrumentBySymbol(symbol);
        
        if (instrument == null) {
            throw new ResourceNotFoundException("Instrument not found: " + symbol);
        }
        
        return toDto(instrument);
    }

    /**
     * Converts domain Instrument model to DTO.
     */
    private InstrumentDto toDto(com.fidelity.integration.hub.adapter.domain.Instrument instrument) {
        return new InstrumentDto(
            instrument.getSymbol(),
            instrument.getName(),
            instrument.getAssetClass(),
            instrument.getExchange(),
            instrument.getCurrentPrice(),
            instrument.getCurrency(),
            instrument.getSecurityId(),
            instrument.getSector(),
            instrument.getIndustry(),
            instrument.getLastUpdated()
        );
    }
}
