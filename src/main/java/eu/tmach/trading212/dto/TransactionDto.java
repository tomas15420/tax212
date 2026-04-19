package eu.tmach.trading212.dto;

import eu.tmach.trading212.model.TradeSide;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Schema(description = "Informace o konkrétní provedené obchodní transakci (nákup/prodej)")
public record TransactionDto(

        @Schema(description = "Interní databázové ID záznamu", example = "1024")
        Long id,

        @Schema(description = "Unikátní identifikátor transakce z platformy Trading 212",
                example = "49566311883")
        String t212id,

        @Schema(description = "Identifikátor cenného papíru (Ticker)",
                example = "AAPL_US_EQ")
        String ticker,

        @Schema(description = "Směr transakce (BUY pro nákup, SELL pro prodej)",
                example = "BUY")
        TradeSide side,

        @Schema(description = "Počet zobchodovaných kusů (může být i frakce)",
                example = "1.52345678")
        BigDecimal quantity,

        @Schema(description = "Realizovaná cena za jeden kus v měně instrumentu",
                example = "185.50")
        BigDecimal price,

        @Schema(description = "Použitý směnný kurz mezi měnou instrumentu a měnou účtu",
                example = "23.45")
        BigDecimal fxRate,

        @Schema(description = "Celková čistá hodnota transakce v měně účtu",
                example = "6625.40")
        BigDecimal netValue,

        @Schema(description = "Datum a čas vyplnění (provedení) objednávky na burze",
                example = "2026-04-19T16:50:19")
        LocalDateTime filledAt,

        @Schema(description = "Detailní informace o souvisejícím instrumentu")
        InstrumentDto instrument
) {
}