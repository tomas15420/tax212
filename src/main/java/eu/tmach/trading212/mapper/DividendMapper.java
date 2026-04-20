package eu.tmach.trading212.mapper;

import eu.tmach.trading212.dto.DividendDto;
import eu.tmach.trading212.dto.trading212.T212DividendItem;
import eu.tmach.trading212.model.Dividend;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface DividendMapper {
    DividendDto toDto(Dividend dividend);

    @Mapping(source = "reference", target = "t212id")
    @Mapping(target = "instrument", ignore = true)
    @Mapping(target = "id", ignore = true)
    Dividend toEntity(T212DividendItem remoteDividend);

    default LocalDateTime mapOffsetToLocal(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }
}
