package eu.tmach.trading212.repository.spec;

import eu.tmach.trading212.dto.filter.TransactionFilter;
import eu.tmach.trading212.model.Instrument;
import eu.tmach.trading212.model.TradeSide;
import eu.tmach.trading212.model.Transaction;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecifications {

    public static Specification<Transaction> withFilter(TransactionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Pokud filtr vůbec neexistuje, vrátíme "vše"
            if (filter == null) {
                return cb.conjunction();
            }

            if (filter.getTicker() != null && !filter.getTicker().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("ticker")),
                        "%" + filter.getTicker().toLowerCase() + "%"
                ));
            }

            // Side (BUY/SELL)
            if (filter.getSide() != null) {
                predicates.add(cb.equal(root.get("side"), filter.getSide()));
            }

            // Datum od
            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("filledAt"),
                        filter.getDateFrom().atStartOfDay()));
            }

            // Datum do
            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("filledAt"),
                        filter.getDateTo().atTime(LocalTime.MAX)));
            }

            Join<Transaction, Instrument> instrumentJoin = root.join("instrument");
            // ISIN přes JOIN na Instrument
            if (filter.getIsin() != null && !filter.getIsin().isBlank()) {
                predicates.add(cb.equal(instrumentJoin.get("isin"), filter.getIsin()));
            }

            // Instrument Name (Like ignore case)
            if (filter.getInstrumentName() != null && !filter.getInstrumentName().isBlank()) {
                predicates.add(cb.like(cb.lower(instrumentJoin.get("name")),
                        "%" + filter.getInstrumentName().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}