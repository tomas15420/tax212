package eu.tmach.trading212.mapper;

import eu.tmach.trading212.dto.InstrumentDto;
import eu.tmach.trading212.dto.trading212.T212Instrument;
import eu.tmach.trading212.model.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {
    InstrumentDto toDto(Instrument instrument);

    @Mapping(target = "id", ignore = true)
    Instrument toEntity(T212Instrument instrument);
}
