package eu.tmach.trading212.service;

import eu.tmach.trading212.client.T212Client;
import eu.tmach.trading212.dto.InstrumentDto;
import eu.tmach.trading212.dto.PagedResponse;
import eu.tmach.trading212.dto.PortfolioStatusDto;
import eu.tmach.trading212.dto.TransactionDto;
import eu.tmach.trading212.dto.filter.TransactionFilter;
import eu.tmach.trading212.dto.trading212.T212OrderWrapper;
import eu.tmach.trading212.mapper.TransactionMapper;
import eu.tmach.trading212.model.Instrument;
import eu.tmach.trading212.model.TradeSide;
import eu.tmach.trading212.model.Transaction;
import eu.tmach.trading212.repository.TransactionRepository;
import eu.tmach.trading212.repository.spec.TransactionSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final T212Client t212Client;
    private final TransactionRepository transactionRepository;
    private final InstrumentService instrumentService;
    private final TransactionMapper transactionMapper;

    @Value("${tax.limits.time-test-years:3}")
    private int timeTestYears;

    public PagedResponse<TransactionDto> getTransactions(TransactionFilter filter) {
        TransactionFilter safeFilter = (filter != null) ? filter : new TransactionFilter();

        Specification<Transaction> spec = TransactionSpecifications.withFilter(safeFilter);

        PagedResponse<Transaction> transactionPage = PagedResponse.from(transactionRepository.findAll(spec, safeFilter.toPageable()));

        return transactionPage.map(transactionMapper::toDto);
    }

    public List<PortfolioStatusDto> getAvailableAssets(LocalDateTime toDate) {
        List<Transaction> buys = transactionRepository.findAllBySideAndFilledAtBefore(TradeSide.BUY, toDate);

        Map<String, List<Transaction>> groupedByTicker = buys.stream()
                .collect(Collectors.groupingBy(Transaction::getTicker));

        return groupedByTicker.entrySet().stream()
                .map(entry -> {
                    String ticker = entry.getKey();
                    List<Transaction> transactions = entry.getValue();

                    BigDecimal taxFree = BigDecimal.ZERO;
                    BigDecimal taxable = BigDecimal.ZERO;

                    for (Transaction t : transactions) {
                        boolean isTaxFree = t.getFilledAt().plusYears(timeTestYears).isBefore(toDate);
                        if (isTaxFree) {
                            taxFree = taxFree.add(t.getRemainingQuantity());
                        } else {
                            taxable = taxable.add(t.getRemainingQuantity());
                        }
                    }
                    BigDecimal totalRemaining = taxFree.add(taxable);

//                    BigDecimal totalRemaining = transactions.stream()
//                            .map(Transaction::getRemainingQuantity)
//                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Instrument instrument = transactions.getFirst().getInstrument();

                    return PortfolioStatusDto.builder()
                            .timeTestYears(timeTestYears)
                            .ticker(ticker)
                            .instrument(instrument != null ?
                                    InstrumentDto.builder()
                                    .name(instrument.getName())
                                    .isin(instrument.getIsin())
                                    .ticker(instrument.getTicker())
                                    .currency(instrument.getCurrency())
                                    .build()
                                    : null)
                            .availableQuantity(totalRemaining)
                            .taxFreeQuantity(taxFree)
                            .inTaxQuantity(taxable)
                            .build();
                })
                .filter(dto -> dto.availableQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    @Transactional
    public void syncAllTransactions() {
        log.info("Kontroluji stav databáze pro synchronizaci");

        String lastSavedT212Id = transactionRepository.findFirstByOrderByFilledAtDesc()
                .map(Transaction::getT212id)
                .orElse(null);

        if (lastSavedT212Id != null) {
            log.info("Poslední nalezené ID v DB: {}. Budu stahovat pouze novější záznamy.", lastSavedT212Id);
        } else {
            log.info("Databáze je prázdná. Spouštím plnou synchronizaci historie.");
        }

        List<T212OrderWrapper> remoteOrders = t212Client.fetchAllOrders(lastSavedT212Id);

        if (remoteOrders.isEmpty()) {
            log.info("Nenalezeny žádné nové transakce k synchronizaci.");
            return;
        }

        Collections.reverse(remoteOrders);

        for (T212OrderWrapper wrapper : remoteOrders) {
            processAndSave(wrapper);
        }

        log.info("Synchronizace dokončena. Celkem zpracováno {} záznamů", remoteOrders.size());
    }

    private void processAndSave(T212OrderWrapper wrapper) {
        Instrument instrument = instrumentService.getOrCreateInstrument(wrapper.order().instrument());

//        Transaction t = Transaction.builder()
//                .t212id(wrapper.fill().id())
//                .instrument(instrument)
//                .ticker(wrapper.order().ticker())
//                .side(wrapper.order().side())
//                .quantity(wrapper.fill().quantity().abs())
//                .price(wrapper.fill().price())
//                .currency(wrapper.order().currency())
//                .fxRate(wrapper.fill().walletImpact().fxRate())
//                .netValue(wrapper.fill().walletImpact().netValue())
//                .filledAt(wrapper.fill().filledAt())
//                .build();

        Transaction t = transactionMapper.toEntity(wrapper);
        t.setInstrument(instrument);

        if (t.getSide() == TradeSide.BUY) {
            t.setRemainingQuantity(t.getQuantity());
        } else {
            t.setRemainingQuantity(BigDecimal.ZERO);
            applyFifo(t);
        }

        transactionRepository.save(t);
    }

    private void applyFifo(Transaction sellTx) {
        List<Transaction> availableBuys = transactionRepository
                .findAllByTickerAndSideAndRemainingQuantityGreaterThanOrderByFilledAtAsc(
                        sellTx.getTicker(), TradeSide.BUY, BigDecimal.ZERO);

        BigDecimal toMatch = sellTx.getQuantity();

        for (Transaction buyTx : availableBuys) {
            if (toMatch.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal buyRemaining = buyTx.getRemainingQuantity();
            BigDecimal quantityToTake = buyRemaining.min(toMatch);

            // Ponížíme zbývající množství u starého nákupu v DB
            buyTx.setRemainingQuantity(buyRemaining.subtract(quantityToTake));
            transactionRepository.save(buyTx);

            sellTx.getMatchedBuys().add(buyTx);

            toMatch = toMatch.subtract(quantityToTake);

            log.info("FIFO: Páruji prodej {} s nákupem {} (zbývá k dopárování: {})",
                    sellTx.getT212id(), buyTx.getT212id(), toMatch);
        }
    }
}
