package eu.tmach.trading212.service;

import eu.tmach.trading212.dto.TaxReportDto;
import eu.tmach.trading212.mapper.DividendMapper;
import eu.tmach.trading212.mapper.TransactionMapper;
import eu.tmach.trading212.model.Dividend;
import eu.tmach.trading212.model.TradeMatch;
import eu.tmach.trading212.model.TradeSide;
import eu.tmach.trading212.model.Transaction;
import eu.tmach.trading212.repository.DividendRepository;
import eu.tmach.trading212.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxService {
    private final TransactionRepository transactionRepository;
    private final DividendRepository dividendRepository;

    private final TransactionMapper transactionMapper;
    private final DividendMapper dividendMapper;

    @Value("${tax.limits.time-test-years:3}")
    private int timeTestYears;

    public TaxReportDto getTaxReport(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59);

        List<Transaction> sales = transactionRepository.findAllBySideAndFilledAtBetween(
                TradeSide.SELL, startOfYear, endOfYear);

        BigDecimal taxFreeSum = BigDecimal.ZERO;
        BigDecimal taxableSum = BigDecimal.ZERO;
        for (Transaction sell : sales) {
            // Vezmeme čistou hodnotu celé transakce
            BigDecimal totalWalletImpact = sell.getWalletImpact();

            for (TradeMatch match : sell.getMatchedBuys()) {
                Transaction buy = match.getBuy();

                BigDecimal portion = match.getMatchedQuantity()
                        .divide(sell.getQuantity(), 10, RoundingMode.HALF_UP);

                // Poměrná část reálné hodnoty (netValue)
                BigDecimal realValuePart = totalWalletImpact.multiply(portion);

                // Časový test 3 roky
                boolean isTaxFree = buy.getFilledAt().plusYears(timeTestYears)
                        .isBefore(sell.getFilledAt());

                if (isTaxFree) {
                    taxFreeSum = taxFreeSum.add(realValuePart);
                } else {
                    taxableSum = taxableSum.add(realValuePart);
                }
            }
        }

        List<Dividend> dividends = dividendRepository.findAllByPaidOnBetween(startOfYear, endOfYear);

        BigDecimal totalDividendsPaid = dividends.stream()
                .map(Dividend::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TaxReportDto.builder()
                .year(year)
                .totalSoldTaxFree(taxFreeSum)
                .totalSoldTaxable(taxableSum)
                .totalDividendsPaid(totalDividendsPaid)
                .transactions(sales.stream().map(transactionMapper::toDto).toList())
                .dividends(dividends.stream().map(dividendMapper::toDto).toList())
                .build();
    }
}
