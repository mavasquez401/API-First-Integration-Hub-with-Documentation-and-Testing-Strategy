package com.fidelity.integration.hub.model.dto;

import com.fidelity.integration.hub.model.enums.AssetClass;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Instrument reference data transfer object.
 * Represents metadata about a financial instrument.
 */
@Schema(description = "Instrument reference data")
public record InstrumentDto(
    @Schema(description = "Instrument symbol/ticker", example = "AAPL")
    String symbol,
    
    @Schema(description = "Instrument name", example = "Apple Inc.")
    String name,
    
    @Schema(description = "Asset class", example = "EQUITY")
    AssetClass assetClass,
    
    @Schema(description = "Exchange where instrument is traded", example = "NASDAQ")
    String exchange,
    
    @Schema(description = "Current market price", example = "175.25")
    BigDecimal currentPrice,
    
    @Schema(description = "Currency code", example = "USD")
    String currency,
    
    @Schema(description = "Security identifier (CUSIP/ISIN)", example = "037833100")
    String securityId,
    
    @Schema(description = "Sector classification", example = "Technology")
    String sector,
    
    @Schema(description = "Industry classification", example = "Consumer Electronics")
    String industry,
    
    @Schema(description = "Date when instrument was last updated")
    LocalDate lastUpdated
) {}
