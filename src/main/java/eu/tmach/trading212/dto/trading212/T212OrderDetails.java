package eu.tmach.trading212.dto.trading212;

import eu.tmach.trading212.model.TradeSide;

public record T212OrderDetails(
        String id,
        String ticker,
        String status,
        TradeSide side,
        String currency,
        T212InstrumentDetail instrument
) {
}