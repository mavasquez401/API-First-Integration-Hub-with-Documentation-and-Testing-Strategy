package com.fidelity.integration.hub.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Portfolio data transfer object.
 * Represents aggregated portfolio information including positions and valuations.
 */
@Schema(description = "Portfolio information with positions and valuations")
public record PortfolioDto(
    @Schema(description = "Account identifier", example = "ACC-12345")
    String accountId,
    
    @Schema(description = "Total portfolio value", example = "125000.50")
    BigDecimal totalValue,
    
    @Schema(description = "Total cost basis", example = "100000.00")
    BigDecimal totalCostBasis,
    
    @Schema(description = "Total unrealized gain/loss", example = "25000.50")
    BigDecimal totalUnrealizedGainLoss,
    
    @Schema(description = "Total unrealized gain/loss percentage", example = "25.00")
    BigDecimal totalUnrealizedGainLossPercent,
    
    @Schema(description = "Currency code", example = "USD")
    String currency,
    
    @Schema(description = "List of positions in the portfolio")
    List<PositionDto> positions,
    
    @Schema(description = "As-of date/time for the portfolio snapshot")
    Instant asOfDate
) {}
