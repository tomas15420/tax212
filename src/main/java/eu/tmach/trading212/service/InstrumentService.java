package eu.tmach.trading212.service;

import eu.tmach.trading212.dto.trading212.T212Instrument;
import eu.tmach.trading212.mapper.InstrumentMapper;
import eu.tmach.trading212.model.Instrument;
import eu.tmach.trading212.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstrumentService {
    private final InstrumentRepository instrumentRepository;

    private final InstrumentMapper instrumentMapper;

    public Instrument getOrCreateInstrument(T212Instrument instrumentDetail) {
        return instrumentRepository.findByTicker(instrumentDetail.ticker())
                .orElseGet(() -> {
                    Instrument newInstrument = instrumentMapper.toEntity(instrumentDetail);
                    return instrumentRepository.save(newInstrument);
                });
    }
}
