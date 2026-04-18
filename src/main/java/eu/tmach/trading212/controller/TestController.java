package eu.tmach.trading212.controller;

import eu.tmach.trading212.dto.*;
import eu.tmach.trading212.dto.filter.TransactionFilter;
import eu.tmach.trading212.exception.ValidationErrorResponse;
import eu.tmach.trading212.service.AccountDetailService;
import eu.tmach.trading212.service.SyncService;
import eu.tmach.trading212.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final TransactionService transactionService;
    private final AccountDetailService accountDetailService;
    private final SyncService syncService;

//    @GetMapping("/syncAll")
//    public ResponseEntity<?> syncALl() {
//        syncService.performFullSync();
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/summary")
//    public ResponseEntity<AccountSummaryDto> getSummary() {
//        return ResponseEntity.ok(accountDetailService.getSummary());
//    }
//
//    @GetMapping("/start")
//    public ResponseEntity<String> startTest() {
//        transactionService.syncAllTransactions();
//        return ResponseEntity.ok().body("Test completed, transactions synchronized.");
//    }
//
//    @GetMapping("/transactions")
//    @Operation(summary = "Získá seznam transakcí s filtrací")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Úspěšné načtení"),
//            @ApiResponse(responseCode = "400", description = "Neplatné parametry filtru",
//                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class)))
//    })
//    public ResponseEntity<PagedResponse<TransactionDto>> testFetch(@ParameterObject @ModelAttribute @Valid TransactionFilter filter) {
//        return ResponseEntity.ok(transactionService.getTransactions(filter));
//    }
//
//    @GetMapping("/portfolio")
//    public ResponseEntity<List<PortfolioStatusDto>> getPortfolio(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
//        return ResponseEntity.ok(transactionService.getAvailableAssets(date == null ? LocalDateTime.now() : date));
//    }
//
//    @GetMapping("/report/{year}")
//    public ResponseEntity<TaxReportDto> getYearlyReport(@PathVariable int year) {
//        return ResponseEntity.ok(transactionService.getTaxReport(year));
//    }
}