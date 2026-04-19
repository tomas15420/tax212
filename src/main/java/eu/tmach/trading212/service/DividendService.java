package eu.tmach.trading212.service;

import eu.tmach.trading212.client.T212Client;
import eu.tmach.trading212.dto.DividendDto;
import eu.tmach.trading212.dto.InstrumentDto;
import eu.tmach.trading212.dto.PagedResponse;
import eu.tmach.trading212.dto.filter.DividendFilter;
import eu.tmach.trading212.dto.trading212.T212DividendItem;
import eu.tmach.trading212.model.Dividend;
import eu.tmach.trading212.model.Instrument;
import eu.tmach.trading212.repository.DividendRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            log.info("Nenalezeny žádné nové transakce k synchronizaci.");
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

        Dividend t = Dividend.builder()
                .amount(remoteDividend.amount())
                .amountInEuro(remoteDividend.amountInEuro())
                .type(remoteDividend.type())
                .paidOn(remoteDividend.paidOn().toLocalDateTime())
                .grossAmountPerShare(remoteDividend.grossAmountPerShare())
                .instrument(instrument)
                .ticker(remoteDividend.ticker())
                .t212id(remoteDividend.reference())
                .currency(remoteDividend.currency())
                .quantity(remoteDividend.quantity())
                .build();

        dividendRepository.save(t);
    }

    public PagedResponse<DividendDto> getDividends(DividendFilter filter) {
        DividendFilter safeFilter = (filter != null) ? filter : new DividendFilter();

        //Specification<Dividend> spec = TransactionSpecifications.withFilter(safeFilter);

        PagedResponse<Dividend> dividendPage = PagedResponse.from(dividendRepository.findAll(safeFilter.toPageable()));

        return dividendPage.map(this::mapToDto);
    }

    private DividendDto mapToDto(Dividend d) {
        return DividendDto.builder()
                .id(d.getId())
                .quantity(d.getQuantity())
                .paidOn(d.getPaidOn())
                .amount(d.getAmount())
                .amountInEuro(d.getAmountInEuro())
                .grossAmountPerShare(d.getGrossAmountPerShare())
                .type(d.getType())
                .ticker(d.getTicker())
                .t212id(d.getT212id())
                .currency(d.getCurrency())
                .instrument(InstrumentDto.builder()
                        .isin(d.getInstrument().getIsin())
                        .name(d.getInstrument().getName())
                        .currency(d.getInstrument().getCurrency())
                        .ticker(d.getInstrument().getTicker())
                        .build())
                .build();
    }

    public List<T212DividendItem> showDividends() {
        return t212Client.fetchAllDividends(null);
    }
}
