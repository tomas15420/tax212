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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountDetailService {
    private final AccountDetailRepository accountDetailRepository;
    private final AccountSummaryMapper accountSummaryMapper;
    private final T212Client t212Client;

    @Transactional
    public void syncAccountSummary() {
        log.info("Synchronizuji stav účtu");
        T212AccountSummary t212AccountSummary = t212Client.getAccountSummary();


        save(accountSummaryMapper.toDto(t212AccountSummary));
    }

    public void save(AccountSummaryDto dto) {
        save(AccountDetailKey.CURRENCY, dto.currency());
        save(AccountDetailKey.TOTAL_VALUE, dto.totalValue().toString());
        save(AccountDetailKey.CURRENT_VALUE, dto.currentValue().toString());
        save(AccountDetailKey.TOTAL_COST, dto.totalCost().toString());
        save(AccountDetailKey.REALIZED_PROFIT_LOSS, dto.realizedProfitLoss().toString());
        save(AccountDetailKey.UNREALIZED_PROFIT_LOSS, dto.unrealizedProfitLoss().toString());
    }

    private void save(AccountDetailKey key, String value) {
        accountDetailRepository.save(new AccountDetail(key.getDbKey(), value));
    }

    public AccountSummaryDto getSummary() {
        Map<String, String> map = accountDetailRepository.findAll().stream()
                .collect(Collectors.toMap(AccountDetail::getKey, AccountDetail::getValue));

        if (map.isEmpty()) return null;

        return AccountSummaryDto.builder()
                .currency(get(map, AccountDetailKey.CURRENCY))
                .totalValue(new BigDecimal(get(map, AccountDetailKey.TOTAL_VALUE)))
                .currentValue(new BigDecimal(get(map, AccountDetailKey.CURRENT_VALUE)))
                .totalCost(new BigDecimal(get(map, AccountDetailKey.TOTAL_COST)))
                .realizedProfitLoss(new BigDecimal(get(map, AccountDetailKey.REALIZED_PROFIT_LOSS)))
                .unrealizedProfitLoss(new BigDecimal(get(map, AccountDetailKey.UNREALIZED_PROFIT_LOSS)))
                .build();
    }

    private String get(Map<String, String> map, AccountDetailKey key) {
        return map.getOrDefault(key.getDbKey(), null);
    }
}
