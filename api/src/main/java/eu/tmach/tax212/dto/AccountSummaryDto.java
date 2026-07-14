package eu.tmach.tax212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Schema(description = "Finanční souhrn investičního portfolia")
@Builder
public record AccountSummaryDto(

        @Schema(description = "Kód měny účtu", example = "CZK")
        String currency,

        @Schema(description = "Celková hodnota majetku (aktuální cena aktiv + hotovost)")
        BigDecimal totalValue,

        @Schema(description = "Aktuální tržní hodnota držených pozic")
        BigDecimal currentValue,

        @Schema(description = "Celkové historické náklady na pořízení (nákupní ceny)")
        BigDecimal totalCost,

        @Schema(description = "Skutečný realizovaný zisk nebo ztráta (z uzavřených obchodů)")
        BigDecimal actualRealizedProfitLoss,

        @Schema(description = "Realizovaný zisk nebo ztráta (z uzavřených obchodů)")
        BigDecimal realizedProfitLoss,

        @Schema(description = "Nerealizovaný (papírový) zisk nebo ztráta (rozdíl tržní ceny a nákladů)")
        BigDecimal unrealizedProfitLoss
) {
}