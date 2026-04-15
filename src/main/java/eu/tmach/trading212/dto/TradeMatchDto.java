package eu.tmach.trading212.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TradeMatchDto(
        BigDecimal matchedQuantity,
        TransactionDto relatedTransaction
) {
}
