package com.fidelity.integration.hub.adapter.domain;

import com.fidelity.integration.hub.model.enums.AssetClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Domain model representing a position from the OMS system.
 * This is the internal representation used between adapters and services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private String symbol;
    private String instrumentName;
    private AssetClass assetClass;
    private BigDecimal quantity;
    private BigDecimal costBasisPerShare;
    private String currency;
}
