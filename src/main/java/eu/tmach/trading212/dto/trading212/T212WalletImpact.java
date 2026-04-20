package eu.tmach.trading212.dto.trading212;

import java.math.BigDecimal;
import java.util.List;

public record T212WalletImpact(
        String currency,
        BigDecimal fxRate,
        BigDecimal netValue,
        BigDecimal realisedProfitLoss,
        List<T212Taxes> taxes
) {
}