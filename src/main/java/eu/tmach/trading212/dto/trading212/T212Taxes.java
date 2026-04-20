package eu.tmach.trading212.dto.trading212;

import java.math.BigDecimal;

public record T212Taxes(
        String name,
        String currency,
        String chargedAt,
        BigDecimal quantity
) {
}
