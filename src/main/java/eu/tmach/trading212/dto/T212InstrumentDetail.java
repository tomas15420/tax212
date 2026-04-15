package eu.tmach.trading212.dto;

public record T212InstrumentDetail(
        String ticker,
        String name,
        String isin,
        String currency
) {
}
