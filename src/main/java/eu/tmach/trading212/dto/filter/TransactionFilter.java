package eu.tmach.trading212.dto.filter;

import eu.tmach.trading212.model.TradeSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder // Nutné pro dědičnost builderu
@NoArgsConstructor // Pro Spring
@AllArgsConstructor // Pokud potřebuješ Builder
@ValidSortFields(
        allowed = {
                "id",
                "ticker",
                "side",
                "quantity",
                "price",
                "netValue",
                "filledAt",
                "instrument.name"
        },
        message = "Řazení je povoleno pouze pro pole: id, ticker, side, quantity, price, netValue, filledAt, instrument.name"
)
public class TransactionFilter extends PageableFilter {
    @Size(min = 1, max = 20, message = "Ticker musí mít 1 až 20 znaků")
    @Schema(description = "Ticker symbol instrumentu (vyhledávání like)", example = "DHER_US_EQ")
    private String ticker;

    @Schema(description = "Strana obchodu (BUY/SELL)", example = "BUY")
    private TradeSide side;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @PastOrPresent(message = "Počáteční datum nesmí být v budoucnosti")
    @Schema(description = "Datum od (včetně) ve formátu YYYY-MM-DD")
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @PastOrPresent(message = "Koncové datum nesmí být v budoucnosti")
    @Schema(description = "Datum do (včetně) ve formátu YYYY-MM-DD")
    private LocalDate dateTo;

    @Size(min = 12, max = 12, message = "ISIN musí mít přesně 12 znaků")
    @Schema(description = "Mezinárodní identifikační číslo ISIN", example = "DE000A2E4K43")
    private String isin;

    @Size(min = 2, message = "Název instrumentu musí mít alespoň 2 znaky")
    @Schema(description = "Název instrumentu (vyhledávání like)", example = "Apple")
    private String instrumentName;

    @Override
    protected String getDefaultSortField() {
        return "filledAt";
    }
}