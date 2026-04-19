package eu.tmach.trading212.dto.trading212;

import eu.tmach.trading212.model.DividendType;

public record T212DividendItem(
        Double amount,
        Double amountInEuro,
        String currency,
        Double grossAmountPerShare,
        T212Instrument instrument,
        String paidOn,
        Double quantity,
        String reference,
        String ticker,
        DividendType type
) {
}