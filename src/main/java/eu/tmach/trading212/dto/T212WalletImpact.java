package eu.tmach.trading212.dto;

import java.math.BigDecimal;

public record T212WalletImpact(
        String currency,
        BigDecimal fxRate,
        BigDecimal netValue
) {
}