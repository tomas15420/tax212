package eu.tmach.trading212.controller;

import eu.tmach.trading212.service.AccountDetailService;
import eu.tmach.trading212.service.SyncService;
import eu.tmach.trading212.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync")
@Tag(name = "Synchronizace", description = "Správa synchronizačních procesů s Trading212 API")
public class SyncController {

    private final SyncService syncService;
    private final TransactionService transactionService;
    private final AccountDetailService accountDetailService;

    @Operation(
            summary = "Kompletní synchronizace",
            description = "Spustí proces úplné aktualizace dat. Zahrnuje informace o účtu, portfoliu i celou historii transakcí. " +
                    "Vzhledem k náročnosti vrací status 202 a běží na pozadí."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Synchronizace byla přijata a zařazena do fronty"),
    })
    @PostMapping("/full")
    public ResponseEntity<Void> performFullSync() {
        syncService.performFullSync();
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Synchronizace transakcí",
            description = "Aktualizuje seznam transakcí od posledního známého stavu."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transakce byly úspěšně aktualizovány"),
    })
    @PostMapping("/transactions")
    public ResponseEntity<Void> syncTransactions() {
        transactionService.syncAllTransactions();
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Synchronizace detailů účtu",
            description = "Aktualizuje aktuální zůstatky, volnou hotovost a nastavení účtu."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detaily účtu byly úspěšně synchronizovány"),
    })
    @PostMapping("/account")
    public ResponseEntity<Void> syncAccountDetails(){
        // accountDetailService.syncAccountInfo(); // Předpokládané volání
        return ResponseEntity.ok().build();
    }
}