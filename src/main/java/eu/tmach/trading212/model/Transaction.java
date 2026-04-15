package eu.tmach.trading212.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_ticker_side", columnList = "ticker, side"),
        @Index(name = "idx_filled_at", columnList = "filledAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String t212id;

    @Column(nullable = false, length = 30)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TradeSide side;

    @Column(nullable = false)
    private LocalDateTime filledAt;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal fxRate;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal netValue;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal remainingQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @ManyToMany
    @JoinTable(
            name = "trade_matches",
            joinColumns = @JoinColumn(name = "sell_id"),
            inverseJoinColumns = @JoinColumn(name = "buy_id")
    )
    @Builder.Default
    private List<Transaction> matchedBuys = new ArrayList<>();

    @ManyToMany(mappedBy = "matchedBuys")
    @Builder.Default
    private List<Transaction> matchedSells = new ArrayList<>();
}
