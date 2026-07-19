package eu.tmach.tax212.repository.spec;

import eu.tmach.tax212.dto.filter.DividendFilter;
import eu.tmach.tax212.model.Dividend;
import eu.tmach.tax212.model.Instrument;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DividendSpecification {
    public static Specification<Dividend> withFilter(DividendFilter filter) {
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

            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("paidOn"),
                        filter.getDateFrom().atStartOfDay()));
            }

            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paidOn"),
                        filter.getDateTo().atTime(LocalTime.MAX)));
            }

            boolean hasIsin = filter.getIsin() != null && !filter.getIsin().isBlank();
            boolean hasInstrumentName = filter.getInstrumentName() != null && !filter.getInstrumentName().isBlank();

            if (hasIsin || hasInstrumentName) {
                Join<Dividend, Instrument> instrumentJoin = root.join("instrument");

                if (hasIsin) {
                    predicates.add(cb.equal(instrumentJoin.get("isin"), filter.getIsin()));
                }

                if (hasInstrumentName) {
                    predicates.add(cb.like(
                            cb.lower(instrumentJoin.get("name")),
                            "%" + filter.getInstrumentName().toLowerCase() + "%"
                    ));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
