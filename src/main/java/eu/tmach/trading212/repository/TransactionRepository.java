package eu.tmach.trading212.repository;

import eu.tmach.trading212.model.TradeSide;
import eu.tmach.trading212.model.Transaction;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findFirstByOrderByFilledAtDesc();

    List<Transaction> findAllByTickerAndSideAndRemainingQuantityGreaterThanOrderByFilledAtAsc(
            String ticker, TradeSide side, BigDecimal remainingQuantity);

    @EntityGraph(attributePaths = {"instrument"})
    List<Transaction> findAllBySideAndFilledAtBefore(TradeSide tradeSide, LocalDateTime toDate);

    List<Transaction> findAllBySideAndFilledAtBetween(TradeSide side, LocalDateTime filledAtAfter, LocalDateTime filledAtBefore);

    @EntityGraph(attributePaths = {"instrument"})
    @NonNull
    List<Transaction> findAll();
    
    @EntityGraph(attributePaths = {"instrument"})
    List<Transaction> findAllByFilledAtBefore(LocalDateTime filledAtBefore);
}
