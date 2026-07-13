package eu.tmach.tax212.dto.trading212;

import java.math.BigDecimal;

public record T212Taxes(
        String name,
        String currency,
        String chargedAt,
        BigDecimal quantity
) {
}
