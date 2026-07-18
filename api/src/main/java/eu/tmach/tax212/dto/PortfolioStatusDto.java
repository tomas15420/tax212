package eu.tmach.tax212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Stav portfolia")
@Builder
public record PortfolioStatusDto(
        @Schema(description = "Doba časového testu")
        Integer holdingPeriodYears,
        @Schema(description = "Roční limit prodeje aktiv")
        Integer assetSaleAnnualCap,
        @Schema(description = "Roční limit okamžitých příjmů")
        Integer incidentalIncomeCap,
        @Schema(description = "Seznam aktiv v portfoliu a jejich stav")
        List<PortfolioStatusItemDto> items) {
}
