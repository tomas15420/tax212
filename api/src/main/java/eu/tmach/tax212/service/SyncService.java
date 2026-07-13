package eu.tmach.tax212.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncService {
    private final TransactionService transactionService;
    private final AccountDetailService accountDetailService;
    private final DividendService dividendService;

    @Transactional
    public void performFullSync() {
        log.info("Spouštím plnou synchronizaci");

        log.info("Spuštím synchronizaci transakcí");
        transactionService.syncAllTransactions();

        log.info("Spouštím synchronizaci dividend");
        dividendService.syncAllDividends();

        log.info("Spouštím synchronizaci účtu");
        accountDetailService.syncAccountSummary();

        log.info("Kompletní synchronizace dokončena");
    }
}
