package eu.tmach.tax212.service;

import eu.tmach.tax212.client.T212Client;
import eu.tmach.tax212.dto.AccountSummaryDto;
import eu.tmach.tax212.dto.trading212.T212AccountSummary;
import eu.tmach.tax212.mapper.AccountSummaryMapper;
import eu.tmach.tax212.model.AccountDetail;
import eu.tmach.tax212.model.AccountDetailKey;
import eu.tmach.tax212.repository.AccountDetailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountDetailService {
    private final AccountDetailRepository accountDetailRepository;
    private final AccountSummaryMapper accountSummaryMapper;
    private final T212Client t212Client;
    private final TransactionService transactionService;

    @Transactional
    public void syncAccountSummary() {
        log.info("Synchronizuji stav účtu");
        T212AccountSummary t212AccountSummary = t212Client.getAccountSummary();

        BigDecimal actualRealizedPnL = transactionService.getActualRealizedProfitLoss();

        AccountSummaryDto dto = accountSummaryMapper.toDto(t212AccountSummary, actualRealizedPnL);
        save(dto);
    }

    public void save(AccountSummaryDto dto) {
        List<AccountDetail> details = List.of(
                createDetail(AccountDetailKey.CURRENCY, dto.currency()),
                createDetail(AccountDetailKey.TOTAL_VALUE, dto.totalValue()),
                createDetail(AccountDetailKey.CURRENT_VALUE, dto.currentValue()),
                createDetail(AccountDetailKey.TOTAL_COST, dto.totalCost()),
                createDetail(AccountDetailKey.ACTUAL_REALIZED_PROFIT_LOSS, dto.actualRealizedProfitLoss()),
                createDetail(AccountDetailKey.REALIZED_PROFIT_LOSS, dto.realizedProfitLoss()),
                createDetail(AccountDetailKey.UNREALIZED_PROFIT_LOSS, dto.unrealizedProfitLoss())
        );

        accountDetailRepository.saveAll(details);
    }

    private AccountDetail createDetail(AccountDetailKey key, Object value) {
        String stringValue = value != null ? value.toString() : "0";
        return new AccountDetail(key.getDbKey(), stringValue);
    }

    public AccountSummaryDto getSummary() {
        Map<String, String> map = accountDetailRepository.findAll().stream()
                .collect(Collectors.toMap(AccountDetail::getKey, AccountDetail::getValue));

        if (map.isEmpty()) return null;

        return AccountSummaryDto.builder()
                .currency(map.getOrDefault(AccountDetailKey.CURRENCY.getDbKey(), "CZK"))
                .totalValue(getAsBigDecimal(map, AccountDetailKey.TOTAL_VALUE))
                .currentValue(getAsBigDecimal(map, AccountDetailKey.CURRENT_VALUE))
                .totalCost(getAsBigDecimal(map, AccountDetailKey.TOTAL_COST))
                .actualRealizedProfitLoss(getAsBigDecimal(map, AccountDetailKey.ACTUAL_REALIZED_PROFIT_LOSS))
                .realizedProfitLoss(getAsBigDecimal(map, AccountDetailKey.REALIZED_PROFIT_LOSS))
                .unrealizedProfitLoss(getAsBigDecimal(map, AccountDetailKey.UNREALIZED_PROFIT_LOSS))
                .build();
    }

    private BigDecimal getAsBigDecimal(Map<String, String> map, AccountDetailKey key) {
        String val = map.get(key.getDbKey());
        if (val == null || val.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            log.error("Chyba při parsování hodnoty pro klíč {}: {}", key, val);
            return BigDecimal.ZERO;
        }
    }
}
