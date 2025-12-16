package com.fidelity.integration.hub.service;

import com.fidelity.integration.hub.model.dto.PortfolioDto;
import com.fidelity.integration.hub.model.dto.PositionDto;
import com.fidelity.integration.hub.adapter.OmsAdapter;
import com.fidelity.integration.hub.adapter.MarketDataVendorAdapter;
import com.fidelity.integration.hub.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for portfolio aggregation and orchestration.
 * Combines data from OMS adapter (positions) and MarketData adapter (prices/valuations).
 */
@Service
public class PortfolioService {

    private final OmsAdapter omsAdapter;
    private final MarketDataVendorAdapter marketDataAdapter;

    public PortfolioService(OmsAdapter omsAdapter, MarketDataVendorAdapter marketDataAdapter) {
        this.omsAdapter = omsAdapter;
        this.marketDataAdapter = marketDataAdapter;
    }

    /**
     * Retrieves portfolio information for an account.
     * Aggregates positions from OMS and enriches with market data.
     * 
     * @param accountId Account identifier
     * @return Portfolio DTO with positions and valuations
     */
    public PortfolioDto getPortfolioByAccount(String accountId) {
        // Fetch positions from OMS
        List<com.fidelity.integration.hub.adapter.domain.Position> positions = omsAdapter.getPositionsByAccount(accountId);
        
        // In production, validate that account exists first
        // For now, empty positions is acceptable (account exists but has no positions)
        
        // Enrich with market data
        List<PositionDto> enrichedPositions = positions.stream()
            .map(position -> enrichPosition(position))
            .collect(Collectors.toList());
        
        // Calculate portfolio-level totals
        BigDecimal totalValue = enrichedPositions.stream()
            .map(PositionDto::positionValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCostBasis = enrichedPositions.stream()
            .map(PositionDto::totalCostBasis)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalUnrealizedGainLoss = totalValue.subtract(totalCostBasis);
        BigDecimal totalUnrealizedGainLossPercent = totalCostBasis.compareTo(BigDecimal.ZERO) > 0
            ? totalUnrealizedGainLoss.divide(totalCostBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
        
        // Determine currency (assuming all positions use same currency - in production, handle multi-currency)
        String currency = enrichedPositions.isEmpty() ? "USD" : enrichedPositions.get(0).currency();
        
        return new PortfolioDto(
            accountId,
            totalValue,
            totalCostBasis,
            totalUnrealizedGainLoss,
            totalUnrealizedGainLossPercent,
            currency,
            enrichedPositions,
            Instant.now()
        );
    }

    /**
     * Enriches a position with current market data.
     */
    private PositionDto enrichPosition(com.fidelity.integration.hub.adapter.domain.Position position) {
        // Fetch current market price for the instrument
        BigDecimal currentPrice = marketDataAdapter.getCurrentPrice(position.getSymbol());
        
        // Calculate derived values
        BigDecimal positionValue = position.getQuantity().multiply(currentPrice);
        BigDecimal totalCostBasis = position.getQuantity().multiply(position.getCostBasisPerShare());
        BigDecimal unrealizedGainLoss = positionValue.subtract(totalCostBasis);
        BigDecimal unrealizedGainLossPercent = position.getCostBasisPerShare().compareTo(BigDecimal.ZERO) > 0
            ? unrealizedGainLoss.divide(totalCostBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;
        
        return new PositionDto(
            position.getSymbol(),
            position.getInstrumentName(),
            position.getAssetClass(),
            position.getQuantity(),
            currentPrice,
            positionValue,
            position.getCostBasisPerShare(),
            totalCostBasis,
            unrealizedGainLoss,
            unrealizedGainLossPercent,
            position.getCurrency()
        );
    }
}
