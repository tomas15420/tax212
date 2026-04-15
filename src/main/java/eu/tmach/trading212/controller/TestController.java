package eu.tmach.trading212.controller;

import eu.tmach.trading212.dto.AccountSummaryDto;
import eu.tmach.trading212.dto.PortfolioStatusDto;
import eu.tmach.trading212.dto.TaxReportDto;
import eu.tmach.trading212.dto.TransactionDto;
import eu.tmach.trading212.service.AccountDetailService;
import eu.tmach.trading212.service.SyncService;
import eu.tmach.trading212.service.TransactionService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/syncAll")
    public ResponseEntity<?> syncALl() {
        syncService.performFullSync();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<AccountSummaryDto> getSummary() {
        return ResponseEntity.ok(accountDetailService.getSummary());
    }

    @GetMapping("/start")
    public ResponseEntity<String> startTest() {
        transactionService.syncAllTransactions();
        return ResponseEntity.ok().body("Test completed, transactions synchronized.");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<TransactionDto>> testFetch() {
        return ResponseEntity.ok(transactionService.getTransactions());
    }

    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioStatusDto>> getPortfolio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(transactionService.getAvailableAssets(date == null ? LocalDateTime.now() : date));
    }

    @GetMapping("/report/{year}")
    public ResponseEntity<TaxReportDto> getYearlyReport(@PathVariable int year) {
        return ResponseEntity.ok(transactionService.getTaxReport(year));
    }
}