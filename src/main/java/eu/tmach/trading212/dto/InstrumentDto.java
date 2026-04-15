package eu.tmach.trading212.dto;

import lombok.Builder;

@Builder
public record InstrumentDto(
        String ticker,
        String name,
        String isin,
        String currency
) {
}
