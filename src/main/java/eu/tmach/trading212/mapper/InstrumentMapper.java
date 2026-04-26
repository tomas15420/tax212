package eu.tmach.trading212.mapper;

import eu.tmach.trading212.dto.InstrumentDto;
import eu.tmach.trading212.dto.trading212.T212Instrument;
import eu.tmach.trading212.model.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    @Mapping(target = "marketTicker", source = "ticker", qualifiedByName = "cleanTicker")
    InstrumentDto toDto(Instrument instrument);

    @Mapping(target = "id", ignore = true)
    Instrument toEntity(T212Instrument instrument);

    @Named("cleanTicker")
    default String cleanTicker(String ticker) {
        if (ticker == null) return null;
        String[] parts = ticker.split("_");
        if (parts.length >= 2 && !"US".equals(parts[1])) {
            return parts[0] + "." + parts[1];
        }
        return parts[0];
    }
}
