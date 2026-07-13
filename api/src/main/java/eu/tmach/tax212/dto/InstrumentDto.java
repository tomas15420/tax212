package eu.tmach.tax212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Základní identifikace finančního nástroje (akcie, ETF apod.)")
@Builder
public record InstrumentDto(

        @Schema(description = "Burzovní symbol (ticker)", example = "VUAA_EU_EQ")
        String ticker,

        @Schema(description = "Čistý tržní symbol", example = "VUAA")
        String marketTicker,

        @Schema(description = "Celý název instrumentu", example = "Vanguard S&P 500 UCITS ETF")
        String name,

        @Schema(description = "Mezinárodní identifikační číslo cenného papíru", example = "IE00BFMXXD54")
        String isin,

        @Schema(description = "Obchodní měna instrumentu", example = "USD")
        String currency
) {
}