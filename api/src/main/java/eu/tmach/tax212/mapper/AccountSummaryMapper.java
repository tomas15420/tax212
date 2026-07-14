package eu.tmach.tax212.mapper;

import eu.tmach.tax212.dto.AccountSummaryDto;
import eu.tmach.tax212.dto.trading212.T212AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AccountSummaryMapper {
    @Mapping(source = "s.investments.currentValue", target = "currentValue")
    @Mapping(source = "s.investments.totalCost", target = "totalCost")
    @Mapping(source = "s.investments.realizedProfitLoss", target = "realizedProfitLoss")
    @Mapping(source = "s.investments.unrealizedProfitLoss", target = "unrealizedProfitLoss")
    @Mapping(source = "actualRealizedProfitLoss", target = "actualRealizedProfitLoss")
    AccountSummaryDto toDto(T212AccountSummary s, BigDecimal actualRealizedProfitLoss);
}