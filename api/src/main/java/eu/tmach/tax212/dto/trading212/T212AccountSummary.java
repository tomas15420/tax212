package eu.tmach.tax212.dto.trading212;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record T212AccountSummary(String currency,
                                 BigDecimal totalValue,
                                 T212Investments investments
) {
}
