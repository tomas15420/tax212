package eu.tmach.trading212.dto;

import eu.tmach.trading212.model.TradeSide;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionDto(
        Long id,
        String t212id,
        String ticker,
        TradeSide side,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal fxRate,
        BigDecimal netValue,
        LocalDateTime filledAt,
        InstrumentDto instrument
) {
}
