package eu.tmach.trading212.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountDetailKey {
    CURRENCY("currency"),
    TOTAL_VALUE("total_value"),
    CURRENT_VALUE("current_value"),
    TOTAL_COST("total_cost"),
    REALIZED_PROFIT_LOSS("realized_profit_loss"),
    UNREALIZED_PROFIT_LOSS("unrealized_profit_loss");

    private final String dbKey;
}
