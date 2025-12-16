package com.fidelity.integration.hub.model.dto;

import com.fidelity.integration.hub.model.enums.AssetClass;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Position data transfer object.
 * Represents a holding position in a portfolio.
 */
@Schema(description = "Portfolio position information")
public record PositionDto(
    @Schema(description = "Instrument symbol", example = "AAPL")
    String symbol,
    
    @Schema(description = "Instrument name", example = "Apple Inc.")
    String instrumentName,
    
    @Schema(description = "Asset class", example = "EQUITY")
    AssetClass assetClass,
    
    @Schema(description = "Quantity held", example = "100.0")
    BigDecimal quantity,
    
    @Schema(description = "Current market price", example = "175.25")
    BigDecimal currentPrice,
    
    @Schema(description = "Total position value (quantity * price)", example = "17525.00")
    BigDecimal positionValue,
    
    @Schema(description = "Cost basis per share", example = "150.00")
    BigDecimal costBasis,
    
    @Schema(description = "Total cost basis", example = "15000.00")
    BigDecimal totalCostBasis,
    
    @Schema(description = "Unrealized gain/loss", example = "2525.00")
    BigDecimal unrealizedGainLoss,
    
    @Schema(description = "Unrealized gain/loss percentage", example = "16.83")
    BigDecimal unrealizedGainLossPercent,
    
    @Schema(description = "Currency code", example = "USD")
    String currency
) {}
