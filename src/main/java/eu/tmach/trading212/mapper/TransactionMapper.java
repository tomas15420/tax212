package eu.tmach.trading212.mapper;

import eu.tmach.trading212.dto.TransactionDto;
import eu.tmach.trading212.dto.trading212.T212OrderWrapper;
import eu.tmach.trading212.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto toDto(Transaction transaction);

    @Mapping(source = "fill.id", target = "t212id")
    @Mapping(source = "order.ticker", target = "ticker")
    @Mapping(source = "order.side", target = "side")
    @Mapping(source = "order.currency", target = "currency")
    @Mapping(source = "fill.price", target = "price")
    @Mapping(source = "fill.filledAt", target = "filledAt")
    @Mapping(source = "fill.walletImpact.fxRate", target = "fxRate")
    @Mapping(source = "fill.walletImpact.netValue", target = "netValue")
    @Mapping(target = "quantity", source = "wrapper")
    @Mapping(target = "instrument", ignore = true)
    @Mapping(target = "remainingQuantity", ignore = true)
    @Mapping(target = "id", ignore = true)
    Transaction toEntity(T212OrderWrapper wrapper);

    default BigDecimal mapQuantity(T212OrderWrapper wrapper) {
        if (wrapper == null || wrapper.fill() == null || wrapper.fill().quantity() == null) {
            return BigDecimal.ZERO;
        }
        return wrapper.fill().quantity().abs();
    }
}
