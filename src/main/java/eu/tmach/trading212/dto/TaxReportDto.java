package eu.tmach.trading212.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record TaxReportDto(
        int year,
        BigDecimal totalSoldTaxFree,    // Splněn časový test (nad 3 roky)
        BigDecimal totalSoldTaxable,   // Nesplněn časový test (pod 3 roky)
        List<TransactionDto> transactions // Seznam všech prodejů v tom roce
) {
}