package eu.tmach.trading212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Souhrnný daňový podklad pro konkrétní kalendářní rok")
@Builder
public record TaxReportDto(

        @Schema(description = "Reportovaný kalendářní rok", example = "2023")
        int year,

        @Schema(
                description = "Celková hodnota prodejů osvobozených od daně (splněn časový test 3 roky)",
                example = "150000.00"
        )
        BigDecimal totalSoldTaxFree,

        @Schema(
                description = "Celková hodnota zdanitelných prodejů (držené méně než 3 roky)",
                example = "45200.50"
        )
        BigDecimal totalSoldTaxable,

        @Schema(
                description = "Celková hrubá výše přijatých dividend v CZK (před zdaněním v zahraničí)",
                example = "12500.00"
        )
        BigDecimal totalDividendsPaid,

        @Schema(description = "Seznam všech realizovaných prodejních transakcí v daném roce")
        List<TransactionDto> transactions,

        @Schema(description = "Seznam všech přijatých dividend v daném roce")
        List<DividendDto> dividends
) {
}