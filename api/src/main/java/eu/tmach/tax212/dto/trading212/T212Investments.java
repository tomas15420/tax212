package eu.tmach.tax212.dto.trading212;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record T212Investments(
        BigDecimal currentValue,
        BigDecimal totalCost,
        BigDecimal realizedProfitLoss,
        BigDecimal unrealizedProfitLoss
) {
}
