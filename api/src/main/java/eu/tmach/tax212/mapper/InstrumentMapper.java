package eu.tmach.tax212.mapper;

import eu.tmach.tax212.dto.InstrumentDto;
import eu.tmach.tax212.dto.trading212.T212Instrument;
import eu.tmach.tax212.model.Instrument;
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
        String clean = ticker.split("_")[0];

        if (clean.length() > 1 && clean.substring(clean.length() - 1).matches("[a-z]")) {
            return clean.substring(0, clean.length() - 1);
        }
        return clean;
    }
}
