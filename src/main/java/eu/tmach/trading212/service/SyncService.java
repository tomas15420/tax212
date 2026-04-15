package eu.tmach.trading212.service;

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

    @Transactional
    public void performFullSync() {
        log.info("Spouštím plnou synchronizaci");
        transactionService.syncAllTransactions();
        accountDetailService.syncAccountSummary();
        log.info("Kompletní synchronizace dokončena");
    }
}
