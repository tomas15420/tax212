package eu.tmach.trading212.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PortfolioStatusDto(
        Integer timeTestYears,
        String ticker,
        InstrumentDto instrument,
        BigDecimal availableQuantity,
        BigDecimal taxFreeQuanitity,
        BigDecimal inTaxQuantity
) {
}
