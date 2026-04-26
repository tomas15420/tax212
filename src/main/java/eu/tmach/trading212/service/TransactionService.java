package eu.tmach.trading212.service;

import eu.tmach.trading212.client.T212Client;
import eu.tmach.trading212.dto.PagedResponse;
import eu.tmach.trading212.dto.PortfolioStatusDto;
import eu.tmach.trading212.dto.TransactionDto;
import eu.tmach.trading212.dto.filter.TransactionFilter;
import eu.tmach.trading212.dto.trading212.T212OrderWrapper;
import eu.tmach.trading212.mapper.InstrumentMapper;
import eu.tmach.trading212.mapper.TransactionMapper;
import eu.tmach.trading212.model.Instrument;
import eu.tmach.trading212.model.TradeMatch;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final T212Client t212Client;
    private final TransactionRepository transactionRepository;
    private final InstrumentService instrumentService;
    private final TransactionMapper transactionMapper;
    private final InstrumentMapper instrumentMapper;

    @Value("${tax.limits.time-test-years:3}")
    private int timeTestYears;

    public PagedResponse<TransactionDto> getTransactions(TransactionFilter filter) {
        TransactionFilter safeFilter = (filter != null) ? filter : new TransactionFilter();

        Specification<Transaction> spec = TransactionSpecifications.withFilter(safeFilter);

        PagedResponse<Transaction> transactionPage = PagedResponse.from(transactionRepository.findAll(spec, safeFilter.toPageable()));

        return transactionPage.map(transactionMapper::toDto);
    }

    public List<PortfolioStatusDto> getAvailableAssets(LocalDateTime toDate) {
        List<Transaction> buys = transactionRepository.findAllByFilledAtBefore(toDate);

        Map<String, List<Transaction>> groupedByTicker = buys.stream()
                .collect(Collectors.groupingBy(Transaction::getTicker));

        return groupedByTicker.entrySet().stream()
                .map(entry -> {
                    String ticker = entry.getKey();
                    List<Transaction> transactions = entry.getValue();

                    BigDecimal taxFree = BigDecimal.ZERO;
                    BigDecimal taxable = BigDecimal.ZERO;

                    BigDecimal totalBuyCost = BigDecimal.ZERO;
                    BigDecimal totalBuyQty = BigDecimal.ZERO;
                    BigDecimal totalRemainingQty = BigDecimal.ZERO;

                    BigDecimal totalSellValue = BigDecimal.ZERO;
                    BigDecimal totalSoldQty = BigDecimal.ZERO;

                    for (Transaction t : transactions) {
                        if (t.getSide() == TradeSide.BUY) {
                            totalBuyQty = totalBuyQty.add(t.getQuantity());

                            BigDecimal remaining = t.getRemainingQuantity();
                            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                                boolean isTaxFree = t.getFilledAt().plusYears(timeTestYears).isBefore(toDate);
                                if (isTaxFree) {
                                    taxFree = taxFree.add(t.getRemainingQuantity());
                                } else {
                                    taxable = taxable.add(t.getRemainingQuantity());
                                }

                                totalBuyCost = totalBuyCost.add(remaining.multiply(t.getPrice().divide(t.getFxRate(), 10, RoundingMode.HALF_UP)));
                                totalRemainingQty = totalRemainingQty.add(remaining);
                            }
                        } else {
                            totalSellValue = totalSellValue.add(t.getQuantity().multiply(t.getPrice()).divide(t.getFxRate(), 10, RoundingMode.HALF_UP));
                            totalSoldQty = totalSoldQty.add(t.getQuantity());
                        }
                    }


                    BigDecimal avgBuy = totalRemainingQty.compareTo(BigDecimal.ZERO) > 0
                            ? totalBuyCost.divide(totalRemainingQty, 10, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    BigDecimal avgSell = totalSoldQty.compareTo(BigDecimal.ZERO) > 0
                            ? totalSellValue.divide(totalSoldQty, 10, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    BigDecimal gainPercent = BigDecimal.ZERO;
                    BigDecimal gainAmount = BigDecimal.ZERO;
                    if (avgBuy.compareTo(BigDecimal.ZERO) > 0 && avgSell.compareTo(BigDecimal.ZERO) > 0) {
                        gainPercent = avgSell.subtract(avgBuy)
                                .divide(avgBuy, 10, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));

                        gainAmount = avgSell.subtract(avgBuy)
                                .multiply(totalSoldQty)
                                .setScale(4, RoundingMode.HALF_UP);
                    }

                    BigDecimal holdingsCost = totalRemainingQty.multiply(avgBuy);

                    Instrument instrument = transactions.getFirst().getInstrument();

                    return PortfolioStatusDto.builder()
                            .timeTestYears(timeTestYears)
                            .ticker(ticker)
                            .instrument(instrumentMapper.toDto(instrument))
                            .availableQuantity(totalRemainingQty)
                            .taxFreeQuantity(taxFree)
                            .inTaxQuantity(taxable)
                            .totalBuysQuantity(totalBuyQty)
                            .totalSellsQuantity(totalSoldQty)
                            .averageBuyPrice(avgBuy.setScale(2, RoundingMode.HALF_UP))
                            .averageSellPrice(avgSell.setScale(2, RoundingMode.HALF_UP))
                            .totalBuyCost(totalBuyCost.setScale(2, RoundingMode.HALF_UP))
                            .totalSellValue(totalSellValue.setScale(2, RoundingMode.HALF_UP))
                            .realisedGainLoss(gainAmount.setScale(2, RoundingMode.HALF_UP))
                            .realizedGainLossPercent(gainPercent.setScale(4, RoundingMode.HALF_UP))
                            .totalHoldingsCost(holdingsCost.setScale(2, RoundingMode.HALF_UP))
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

        Transaction t = transactionMapper.toEntity(wrapper);
        t.setInstrument(instrument);
        t.setTradeValue(t.getPrice().multiply(t.getQuantity()).divide(t.getFxRate(), RoundingMode.HALF_UP));

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
        BigDecimal tradingPnl = BigDecimal.ZERO;
        BigDecimal actualPnl = BigDecimal.ZERO;

        for (Transaction buyTx : availableBuys) {
            if (toMatch.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal buyRemaining = buyTx.getRemainingQuantity();
            BigDecimal quantityToTake = buyRemaining.min(toMatch);

            tradingPnl = tradingPnl.add(calculatePartialPnl(sellTx, buyTx, quantityToTake, Transaction::getWalletImpact));
            actualPnl = actualPnl.add(calculatePartialPnl(sellTx, buyTx, quantityToTake, Transaction::getTradeValue));

            // Ponížíme zbývající množství u starého nákupu v DB
            buyTx.setRemainingQuantity(buyRemaining.subtract(quantityToTake));
            transactionRepository.save(buyTx);

            TradeMatch match = TradeMatch.builder()
                    .sell(sellTx)
                    .buy(buyTx)
                    .matchedQuantity(quantityToTake)
                    .build();

            sellTx.getMatchedBuys().add(match);

            toMatch = toMatch.subtract(quantityToTake);

            log.info("FIFO: Páruji prodej {} s nákupem {} (zbývá k dopárování: {}) | tpnl={} | apnl={}",
                    sellTx.getT212id(), buyTx.getT212id(), toMatch, tradingPnl, actualPnl);
        }
        sellTx.setActualPnl(tradingPnl);
        sellTx.setTradingPnl(actualPnl);
    }

    private BigDecimal calculatePartialPnl(Transaction sell, Transaction buy, BigDecimal qty, Function<Transaction, BigDecimal> fieldExtractor) {
        BigDecimal sellPart = fieldExtractor.apply(sell)
                .multiply(qty)
                .divide(sell.getQuantity(), 10, RoundingMode.HALF_UP);

        BigDecimal buyPart = fieldExtractor.apply(buy)
                .multiply(qty)
                .divide(buy.getQuantity(), 10, RoundingMode.HALF_UP);

        return sellPart.subtract(buyPart);
    }
}
