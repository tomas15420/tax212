package eu.tmach.tax212.repository.spec;

import eu.tmach.tax212.dto.filter.TransactionFilter;
import eu.tmach.tax212.model.Instrument;
import eu.tmach.tax212.model.Transaction;
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

            if (filter == null) {
                return cb.conjunction();
            }

            if (filter.getTicker() != null && !filter.getTicker().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("ticker")),
                        "%" + filter.getTicker().toLowerCase() + "%"
                ));
            }

            if (filter.getSide() != null) {
                predicates.add(cb.equal(root.get("side"), filter.getSide()));
            }

            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("filledAt"),
                        filter.getDateFrom().atStartOfDay()));
            }

            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("filledAt"),
                        filter.getDateTo().atTime(LocalTime.MAX)));
            }

            Join<Transaction, Instrument> instrumentJoin = root.join("instrument");
            if (filter.getIsin() != null && !filter.getIsin().isBlank()) {
                predicates.add(cb.equal(instrumentJoin.get("isin"), filter.getIsin()));
            }

            if (filter.getInstrumentName() != null && !filter.getInstrumentName().isBlank()) {
                predicates.add(cb.like(cb.lower(instrumentJoin.get("name")),
                        "%" + filter.getInstrumentName().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}