package com.fidelity.integration.hub.adapter.domain;

import com.fidelity.integration.hub.model.enums.AssetClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain model representing instrument reference data from market data vendors.
 * This is the internal representation used between adapters and services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instrument {
    private String symbol;
    private String name;
    private AssetClass assetClass;
    private String exchange;
    private BigDecimal currentPrice;
    private String currency;
    private String securityId;
    private String sector;
    private String industry;
    private LocalDate lastUpdated;
}
