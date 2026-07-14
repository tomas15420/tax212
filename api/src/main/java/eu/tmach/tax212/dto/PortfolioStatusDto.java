package eu.tmach.tax212.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Stav portfolia")
@Builder
public record PortfolioStatusDto(
        @Schema(description = "Doba časového testu", example = "3")
        Integer timeTestYears,
        @Schema(description = "Seznam aktiv v portfoliu a jejich stav")
        List<PortfolioStatusItemDto> items) {
}
