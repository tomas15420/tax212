package eu.tmach.tax212.dto.trading212;

import eu.tmach.tax212.model.FillType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record T212Fill(
        String id,
        LocalDateTime filledAt,
        BigDecimal price,
        BigDecimal quantity,
        T212WalletImpact walletImpact,
        FillType type
) {
}