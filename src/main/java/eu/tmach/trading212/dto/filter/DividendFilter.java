package eu.tmach.trading212.dto.filter;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@ValidSortFields(allowed = {
        "paidOn",
        "id",
        "ticker",
        "amount",
        "amountInEuro",
        "grossAmountPerShare",
        "quantity",
        "type"
},
        message = "Řazení je povoleno pouze pro pole: paidOn, id, ticker, amount, amountInEuro, grossAmountPerShare, quantity, type"
)
public class DividendFilter extends PageableFilter {
    @Override
    protected String getDefaultSortField() {
        return "paidOn";
    }
}
