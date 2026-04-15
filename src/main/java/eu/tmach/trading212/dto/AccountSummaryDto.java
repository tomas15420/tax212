package eu.tmach.trading212.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountSummaryDto(
        String currency,
        BigDecimal totalValue,
        BigDecimal currentValue,
        BigDecimal totalCost,
        BigDecimal realizedProfitLoss,
        BigDecimal unrealizedProfitLoss
) {
}
