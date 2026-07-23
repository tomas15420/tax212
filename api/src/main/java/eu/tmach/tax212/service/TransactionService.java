package eu.tmach.tax212.service;

import eu.tmach.tax212.client.T212Client;
import eu.tmach.tax212.config.TaxProperties;
import eu.tmach.tax212.dto.PagedResponse;
import eu.tmach.tax212.dto.PortfolioStatusDto;
import eu.tmach.tax212.dto.PortfolioStatusItemDto;
import eu.tmach.tax212.dto.TransactionDto;
import eu.tmach.tax212.dto.filter.TransactionFilter;
import eu.tmach.tax212.dto.trading212.T212OrderWrapper;
import eu.tmach.tax212.mapper.InstrumentMapper;
import eu.tmach.tax212.mapper.TransactionMapper;
import eu.tmach.tax212.model.Instrument;
import eu.tmach.tax212.model.TradeMatch;
import eu.tmach.tax212.model.TradeSide;
import eu.tmach.tax212.model.Transaction;
import eu.tmach.tax212.repository.TransactionRepository;
import eu.tmach.tax212.repository.spec.TransactionSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
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
    private final TaxProperties taxProperties;

    public PagedResponse<TransactionDto> getTransactions(TransactionFilter filter) {
        TransactionFilter safeFilter = (filter != null) ? filter : new TransactionFilter();
        Specification<Transaction> spec = TransactionSpecifications.withFilter(safeFilter);

        return PagedResponse.from(
                transactionRepository.findAll(spec, safeFilter.toPageable()),
                transactionMapper::toDto
        );
    }

    public PortfolioStatusDto getAvailableAssets(LocalDateTime toDate, boolean includeSold) {
        List<Transaction> buys = transactionRepository.findAllByFilledAtBefore(toDate);

        Map<String, List<Transaction>> groupedByTicker = buys.stream()
                .collect(Collectors.groupingBy(Transaction::getTicker));

        List<PortfolioStatusItemDto> items = groupedByTicker.entrySet().stream()
                .map(entry -> {
                    String ticker = entry.getKey();
                    List<Transaction> tickerTransactions = entry.getValue();

                    BigDecimal taxFreeQty = BigDecimal.ZERO;
                    BigDecimal taxableQty = BigDecimal.ZERO;

                    BigDecimal totalBuyCostForRemaining = BigDecimal.ZERO;
                    BigDecimal totalCostValue = BigDecimal.ZERO;
                    BigDecimal totalBuyQty = BigDecimal.ZERO;
                    BigDecimal totalRemainingQty = BigDecimal.ZERO;

                    BigDecimal totalSellValue = BigDecimal.ZERO;
                    BigDecimal totalSoldQty = BigDecimal.ZERO;
                    BigDecimal totalRealizedPnL = BigDecimal.ZERO;
                    BigDecimal totalActualRealizedPnL = BigDecimal.ZERO;

                    for (Transaction t : tickerTransactions) {
                        if (t.getSide() == TradeSide.BUY) {
                            totalBuyQty = totalBuyQty.add(t.getQuantity());
                            BigDecimal remaining = t.getRemainingQuantity();

                            BigDecimal costValue = t.getQuantity().multiply(t.getPrice())
                                    .divide(t.getFxRate(), 10, RoundingMode.HALF_UP);

                            totalCostValue = totalCostValue.add(costValue);

                            if (remaining != null && remaining.compareTo(BigDecimal.ZERO) > 0) {
                                boolean isTaxFree = t.getFilledAt().plusYears(taxProperties.getHoldingPeriodYears()).isBefore(toDate);
                                if (isTaxFree) {
                                    taxFreeQty = taxFreeQty.add(remaining);
                                } else {
                                    taxableQty = taxableQty.add(remaining);
                                }

                                BigDecimal remainingCostValue = remaining.multiply(t.getPrice())
                                        .divide(t.getFxRate(), 10, RoundingMode.HALF_UP);


                                totalBuyCostForRemaining = totalBuyCostForRemaining.add(remainingCostValue);
                                totalRemainingQty = totalRemainingQty.add(remaining);

                            }
                        } else if (t.getSide() == TradeSide.SELL) {
                            totalSoldQty = totalSoldQty.add(t.getQuantity());

                            BigDecimal sellValue = t.getQuantity().multiply(t.getPrice())
                                    .divide(t.getFxRate(), 10, RoundingMode.HALF_UP);
                            totalSellValue = totalSellValue.add(sellValue);

                            if (t.getActualPnl() != null) {
                                totalActualRealizedPnL = totalActualRealizedPnL.add(t.getActualPnl());
                            }

                            if (t.getTradingPnl() != null) {
                                totalRealizedPnL = totalRealizedPnL.add(t.getTradingPnl());
                            }
                        }
                    }

                    BigDecimal avgBuy = totalBuyQty.compareTo(BigDecimal.ZERO) > 0
                            ? totalCostValue.divide(totalBuyQty, 10, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    BigDecimal avgSell = totalSoldQty.compareTo(BigDecimal.ZERO) > 0
                            ? totalSellValue.divide(totalSoldQty, 10, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;


                    BigDecimal realizedGainPercent = BigDecimal.ZERO;
                    BigDecimal actualGainPercent = BigDecimal.ZERO;
                    if (avgBuy.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal actualCost = totalSellValue.subtract(totalActualRealizedPnL);
                        if (actualCost.compareTo(BigDecimal.ZERO) > 0) {
                            actualGainPercent = totalActualRealizedPnL
                                    .divide(actualCost, 10, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100));
                        }

                        BigDecimal tradingCost = totalSellValue.subtract(totalRealizedPnL);
                        if (tradingCost.compareTo(BigDecimal.ZERO) > 0) {
                            realizedGainPercent = totalRealizedPnL
                                    .divide(tradingCost, 10, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100));
                        }
                    }

                    Instrument instrument = tickerTransactions.stream()
                            .map(Transaction::getInstrument)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

                    return PortfolioStatusItemDto.builder()
                            .ticker(ticker)
                            .instrument(instrumentMapper.toDto(instrument))
                            .availableQuantity(totalRemainingQty)
                            .taxFreeQuantity(taxFreeQty)
                            .inTaxQuantity(taxableQty)
                            .totalBuysQuantity(totalBuyQty)
                            .totalSellsQuantity(totalSoldQty)
                            .averageBuyPrice(avgBuy.setScale(2, RoundingMode.HALF_UP))
                            .averageSellPrice(avgSell.setScale(2, RoundingMode.HALF_UP))
                            .totalBuyCost(totalCostValue.setScale(2, RoundingMode.HALF_UP))
                            .totalSellValue(totalSellValue.setScale(2, RoundingMode.HALF_UP))
                            .realisedGainLoss(totalRealizedPnL.setScale(2, RoundingMode.HALF_UP))
                            .realizedGainLossPercent(realizedGainPercent.setScale(2, RoundingMode.HALF_UP))
                            .actualRealisedGainLoss(totalActualRealizedPnL.setScale(2, RoundingMode.HALF_UP))
                            .actualRealizedGainLossPercent(actualGainPercent.setScale(2, RoundingMode.HALF_UP))
                            .totalHoldingsCost(totalBuyCostForRemaining.setScale(2, RoundingMode.HALF_UP)) // holdings cost je de facto totalBuyCost zbývajících pozic
                            .build();
                })
                .filter(item -> includeSold || item.availableQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        return PortfolioStatusDto.builder()
                .holdingPeriodYears(taxProperties.getHoldingPeriodYears())
                .incidentalIncomeCap(taxProperties.getIncidentalIncomeCap())
                .assetSaleAnnualCap(taxProperties.getAssetSaleAnnualCap())
                .items(items)
                .build();
    }

    public BigDecimal getActualRealizedProfitLoss() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getSide() == TradeSide.SELL)
                .map(Transaction::getActualPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

        ArrayDeque<Transaction> buyQueue = new ArrayDeque<>(availableBuys);

        BigDecimal toMatch = sellTx.getQuantity();
        BigDecimal tradingPnl = BigDecimal.ZERO;
        BigDecimal actualPnl = BigDecimal.ZERO;
        List<Transaction> buyTransactions = new ArrayList<>();

        while (toMatch.compareTo(BigDecimal.ZERO) > 0 && !buyQueue.isEmpty()) {
            Transaction buyTx = buyQueue.poll();

            BigDecimal buyRemaining = buyTx.getRemainingQuantity();
            BigDecimal quantityToTake = buyRemaining.min(toMatch);

            tradingPnl = tradingPnl.add(calculatePartialPnl(sellTx, buyTx, quantityToTake, Transaction::getWalletImpact));
            actualPnl = actualPnl.add(calculatePartialPnl(sellTx, buyTx, quantityToTake, Transaction::getTradeValue));

            buyTx.setRemainingQuantity(buyRemaining.subtract(quantityToTake));
            buyTransactions.add(buyTx);

            if (buyTx.getRemainingQuantity().compareTo(BigDecimal.ZERO) > 0) {
                buyQueue.addFirst(buyTx);
            }

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
        transactionRepository.saveAll(buyTransactions);
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
