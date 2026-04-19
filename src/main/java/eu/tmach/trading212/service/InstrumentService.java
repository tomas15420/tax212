package eu.tmach.trading212.service;

import eu.tmach.trading212.dto.trading212.T212Instrument;
import eu.tmach.trading212.model.Instrument;
import eu.tmach.trading212.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstrumentService {
    private final InstrumentRepository instrumentRepository;

    public Instrument getOrCreateInstrument(T212Instrument instrumentDetail) {
        return instrumentRepository.findByTicker(instrumentDetail.ticker())
                .orElseGet(() -> {
                    Instrument newInstrument = Instrument.builder()
                            .ticker(instrumentDetail.ticker())
                            .name(instrumentDetail.name())
                            .isin(instrumentDetail.isin())
                            .currency(instrumentDetail.currency())
                            .build();
                    return instrumentRepository.save(newInstrument);
                });
    }
}
