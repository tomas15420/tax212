package eu.tmach.tax212.dto;

import eu.tmach.tax212.model.DividendType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Schema(description = "Informace o vyplacené dividendě")
public record DividendDto(

        @Schema(description = "Interní databázové ID záznamu", example = "501")
        Long id,

        @Schema(description = "Unikátní ID transakce z Trading 212 (reference)",
                example = "a7efd38e-02bd-4483-9759-97e1616eb301")
        String t212id,

        @Schema(description = "Ticker symbol instrumentu",
                example = "WMT_US_EQ")
        String ticker,

        @Schema(description = "Datum a čas připsání dividendy na účet",
                example = "2026-04-06T15:43:20")
        LocalDateTime paidOn,

        @Schema(description = "Čistá částka dividendy v primární měně účtu",
                example = "2.38")
        BigDecimal amount,

        @Schema(description = "Čistá částka dividendy přepočtená na EUR",
                example = "0.097123")
        BigDecimal amountInEuro,

        @Schema(description = "Kód primární měny účtu",
                example = "CZK")
        String currency,

        @Schema(description = "Hrubá výše dividendy na jednu akcii v měně instrumentu",
                example = "0.2475")
        BigDecimal grossAmountPerShare,

        @Schema(description = "Počet držených kusů (včetně frakcí) v rozhodný den",
                example = "0.53309001")
        BigDecimal quantity,

        @Schema(description = "Typ dividendové události",
                example = "DIVIDEND")
        DividendType type,

        @Schema(description = "Detailní informace o akcii, ke které se dividenda váže")
        InstrumentDto instrument
) {
}