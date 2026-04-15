package eu.tmach.trading212.dto.trading212;

import java.math.BigDecimal;

public record T212WalletImpact(
        String currency,
        BigDecimal fxRate,
        BigDecimal netValue
) {
}