package eu.tmach.trading212.dto.trading212;

import eu.tmach.trading212.model.OrderStatus;
import eu.tmach.trading212.model.TradeSide;

public record T212OrderDetails(
        String id,
        String ticker,
        OrderStatus status,
        TradeSide side,
        String currency,
        T212InstrumentDetail instrument
) {
}