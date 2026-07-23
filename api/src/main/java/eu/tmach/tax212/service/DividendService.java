package eu.tmach.tax212.service;

import eu.tmach.tax212.client.T212Client;
import eu.tmach.tax212.dto.DividendDto;
import eu.tmach.tax212.dto.PagedResponse;
import eu.tmach.tax212.dto.filter.DividendFilter;
import eu.tmach.tax212.dto.trading212.T212DividendItem;
import eu.tmach.tax212.mapper.DividendMapper;
import eu.tmach.tax212.model.Dividend;
import eu.tmach.tax212.model.Instrument;
import eu.tmach.tax212.repository.DividendRepository;
import eu.tmach.tax212.repository.spec.DividendSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DividendService {
    private final T212Client t212Client;

    private final DividendRepository dividendRepository;

    private final InstrumentService instrumentService;

    private final DividendMapper dividendMapper;

    @Transactional
    public void syncAllDividends() {
        log.info("Kontroluji stav databáze pro synchronizaci");

        String lastSavedT212Id = dividendRepository.findFirstByOrderByPaidOnDesc()
                .map(Dividend::getT212id)
                .orElse(null);

        if (lastSavedT212Id != null) {
            log.info("Poslední nalezené ID v DB: {}. Budu stahovat pouze novější záznamy.", lastSavedT212Id);
        } else {
            log.info("Databáze je prázdná. Spouštím plnou synchronizaci historie.");
        }

        List<T212DividendItem> remoteDividends = t212Client.fetchAllDividends(lastSavedT212Id);

        if (remoteDividends.isEmpty()) {
            log.info("Nenalezeny žádné nové dividendy k synchronizaci.");
            return;
        }

        Collections.reverse(remoteDividends);

        for (T212DividendItem dividend : remoteDividends) {
            processAndSave(dividend);
        }

        log.info("Synchronizace dokončena. Celkem zpracováno {} záznamů", remoteDividends.size());
    }

    private void processAndSave(T212DividendItem remoteDividend) {
        Instrument instrument = instrumentService.getOrCreateInstrument(remoteDividend.instrument());

        Dividend dividend = dividendMapper.toEntity(remoteDividend);

        dividend.setInstrument(instrument);

        dividendRepository.save(dividend);
    }

    public PagedResponse<DividendDto> getDividends(DividendFilter filter) {
        DividendFilter safeFilter = (filter != null) ? filter : new DividendFilter();

        Specification<Dividend> spec = DividendSpecification.withFilter(safeFilter);

        return PagedResponse.from(
                dividendRepository.findAll(spec, safeFilter.toPageable()),
                dividendMapper::toDto
        );
    }

    public List<T212DividendItem> showDividends() {
        return t212Client.fetchAllDividends(null);
    }
}
