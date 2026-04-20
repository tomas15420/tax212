package eu.tmach.trading212.mapper;

import eu.tmach.trading212.dto.AccountSummaryDto;
import eu.tmach.trading212.dto.trading212.T212AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountSummaryMapper {

    @Mapping(source = "investments.currentValue", target = "currentValue")
    @Mapping(source = "investments.totalCost", target = "totalCost")
    @Mapping(source = "investments.realizedProfitLoss", target = "realizedProfitLoss")
    @Mapping(source = "investments.unrealizedProfitLoss", target = "unrealizedProfitLoss")
    AccountSummaryDto toDto(T212AccountSummary s);
}