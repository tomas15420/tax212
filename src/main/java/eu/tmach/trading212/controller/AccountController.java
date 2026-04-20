package eu.tmach.trading212.controller;

import eu.tmach.trading212.dto.AccountSummaryDto;
import eu.tmach.trading212.dto.PortfolioStatusDto;
import eu.tmach.trading212.dto.TaxReportDto;
import eu.tmach.trading212.service.AccountDetailService;
import eu.tmach.trading212.service.TaxService;
import eu.tmach.trading212.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Tag(name = "Účty a Portfolio", description = "Správa investičního účtu, přehledy aktiv a daňové podklady")
public class AccountController {
    private final AccountDetailService accountDetailService;
    private final TransactionService transactionService;
    private final TaxService taxService;

    @Operation(
            summary = "Získat souhrn účtu",
            description = "Vrátí celkový finanční přehled investičního účtu včetně zisků, ztrát a nákladů."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Úspěšně načtený přehled účtu",
                    content = @Content(schema = @Schema(implementation = AccountSummaryDto.class))
            ),
    })
    @GetMapping("/summary")
    public ResponseEntity<AccountSummaryDto> getSummary() {
        return ResponseEntity.ok(accountDetailService.getSummary());
    }

    @Operation(
            summary = "Získat stav portfolia",
            description = "Vrátí seznam držených aktiv k určenému datu. Pokud datum není zadáno, vrátí aktuální stav. " +
                    "Výpočet zohledňuje časový test pro daňové účely."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Úspěšně načtený stav portfolia",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PortfolioStatusDto.class)))
            ),
    })
    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioStatusDto>> getPortfolio(
            @Parameter(description = "Datum a čas, ke kterému se má stav portfolia vypočítat. Formát ISO",
                    example = "2024-12-31T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(transactionService.getAvailableAssets(date == null ? LocalDateTime.now() : date));
    }

    @Operation(
            summary = "Generovat roční daňový report",
            description = "Vypočítá sumární hodnoty prodejů rozdělené na ty, které splňují časový test pro osvobození od daně, a ty, které podléhají zdanění."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Daňový report úspěšně vygenerován",
                    content = @Content(schema = @Schema(implementation = TaxReportDto.class))
            ),
    })
    @GetMapping("/tax-reports/{year}")
    public ResponseEntity<TaxReportDto> getYearlyReport(
            @Parameter(description = "Kalendářní rok, za který se report generuje", example = "2023")
            @PathVariable Integer year) {
        return ResponseEntity.ok(taxService.getTaxReport(year));
    }
}