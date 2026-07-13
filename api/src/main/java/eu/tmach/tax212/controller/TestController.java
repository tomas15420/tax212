package eu.tmach.tax212.controller;

import eu.tmach.tax212.service.AccountDetailService;
import eu.tmach.tax212.service.DividendService;
import eu.tmach.tax212.service.SyncService;
import eu.tmach.tax212.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TransactionService transactionService;
    private final AccountDetailService accountDetailService;
    private final SyncService syncService;
    private final DividendService dividendService;
}