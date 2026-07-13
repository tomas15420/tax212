package eu.tmach.tax212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Finanční souhrn investičního portfolia")
@Builder
public record AccountSummaryDto(

        @Schema(description = "Kód měny, ve které je souhrn vypočten", example = "CZK")
        String currency,

        @Schema(description = "Celková hodnota majetku (aktuální cena aktiv + hotovost)", example = "1500250.00")
        BigDecimal totalValue,

        @Schema(description = "Aktuální tržní hodnota držených pozic", example = "1200000.50")
        BigDecimal currentValue,

        @Schema(description = "Celkové historické náklady na pořízení (nákupní ceny)", example = "1000000.00")
        BigDecimal totalCost,

        @Schema(description = "Realizovaný zisk nebo ztráta (z uzavřených obchodů)", example = "50250.00")
        BigDecimal realizedProfitLoss,

        @Schema(description = "Nerealizovaný (papírový) zisk nebo ztráta (rozdíl tržní ceny a nákladů)", example = "200000.50")
        BigDecimal unrealizedProfitLoss
) {
}