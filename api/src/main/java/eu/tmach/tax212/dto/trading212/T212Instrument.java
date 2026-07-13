package eu.tmach.tax212.dto.trading212;

public record T212Instrument(
        String ticker,
        String name,
        String isin,
        String currency
) {
}
