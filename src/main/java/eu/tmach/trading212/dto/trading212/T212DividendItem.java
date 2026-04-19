package eu.tmach.trading212.dto.trading212;

import eu.tmach.trading212.model.DividendType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record T212DividendItem(
        BigDecimal amount,
        BigDecimal amountInEuro,
        String currency,
        BigDecimal grossAmountPerShare,
        T212Instrument instrument,
        OffsetDateTime paidOn,
        BigDecimal quantity,
        String reference,
        String ticker,
        DividendType type
) {
}