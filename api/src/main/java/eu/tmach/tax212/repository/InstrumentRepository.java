package eu.tmach.tax212.repository;

import eu.tmach.tax212.model.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
    Optional<Instrument> findByTicker(String ticker);
}
