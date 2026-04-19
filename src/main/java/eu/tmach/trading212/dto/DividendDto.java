package eu.tmach.trading212.dto;

import eu.tmach.trading212.model.DividendType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DividendDto(
        Long id,
        String t212id,
        String ticker,
        LocalDateTime paidOn,
        BigDecimal amount,
        BigDecimal amountInEuro,
        String currency,
        BigDecimal grossAmountPerShare,
        BigDecimal quantity,
        DividendType type,
        InstrumentDto instrument
) {
}