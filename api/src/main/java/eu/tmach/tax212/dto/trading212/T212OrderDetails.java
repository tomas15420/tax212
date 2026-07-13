package eu.tmach.tax212.dto.trading212;

import eu.tmach.tax212.model.OrderStatus;
import eu.tmach.tax212.model.TradeSide;

public record T212OrderDetails(
        String id,
        String ticker,
        OrderStatus status,
        TradeSide side,
        String currency,
        T212Instrument instrument
) {
}