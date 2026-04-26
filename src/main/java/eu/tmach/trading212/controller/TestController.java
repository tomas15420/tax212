package eu.tmach.trading212.controller;

import eu.tmach.trading212.service.AccountDetailService;
import eu.tmach.trading212.service.DividendService;
import eu.tmach.trading212.service.SyncService;
import eu.tmach.trading212.service.TransactionService;
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