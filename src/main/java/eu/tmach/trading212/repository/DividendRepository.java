package eu.tmach.trading212.repository;

import eu.tmach.trading212.model.Dividend;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DividendRepository extends JpaRepository<Dividend, Long> {
    Optional<Dividend> findFirstByOrderByPaidOnDesc();

    @EntityGraph(attributePaths = {"instrument"})
    @NonNull
    List<Dividend> findAll();
}
