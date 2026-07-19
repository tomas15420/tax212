package eu.tmach.tax212.repository;

import eu.tmach.tax212.model.Dividend;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long>, JpaSpecificationExecutor<Dividend> {
    Optional<Dividend> findFirstByOrderByPaidOnDesc();

    @EntityGraph(attributePaths = {"instrument"})
    @NonNull
    List<Dividend> findAll();

    List<Dividend> findAllByPaidOnBetween(LocalDateTime from, LocalDateTime to);
}
