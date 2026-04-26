package eu.tmach.trading212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Detailní stav konkrétního aktiva v portfoliu")
@Builder
public record PortfolioStatusDto(

        @Schema(description = "Počet let pro splnění časového testu (typicky 3 roky v ČR)", example = "3")
        Integer timeTestYears,

        @Schema(description = "Burzovní symbol aktiva", example = "AAPL")
        String ticker,

        @Schema(description = "Detailní informace o instrumentu")
        InstrumentDto instrument,

        @Schema(description = "Celkové aktuálně držené množství", example = "10.55")
        BigDecimal availableQuantity,

        @Schema(description = "Množství, které již splnilo časový test a je osvobozeno od daně", example = "4.20")
        BigDecimal taxFreeQuantity,

        @Schema(description = "Množství, které je stále v daňovém testu (držené kratší dobu)", example = "6.35")
        BigDecimal inTaxQuantity,

        @Schema(description = "Průměrná nákupní cena držených akcií", example = "150.25")
        BigDecimal averageBuyPrice,

        @Schema(description = "Průměrná prodejní cena", example = "165.50")
        BigDecimal averageSellPrice,

        @Schema(description = "Rozdíl mezi průměrným prodejem a nákupem (v %)", example = "0.52")
        BigDecimal historicalGainPercent,

        @Schema(description = "Celková nákupní hodnota držených pozic v CZK", example = "13962.70")
        BigDecimal totalHoldingsCost
) {
}
