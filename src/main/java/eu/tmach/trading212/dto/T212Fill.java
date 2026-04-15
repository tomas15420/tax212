package eu.tmach.trading212.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record T212Fill(
        String id,
        LocalDateTime filledAt,
        BigDecimal price,
        BigDecimal quantity,
        T212WalletImpact walletImpact
) {
}