package eu.tmach.tax212.model;

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
    private BigDecimal walletImpact;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal tradeValue;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal remainingQuantity;

    @Column(precision = 19, scale = 8)
    private BigDecimal tradingPnl;

    @Column(precision = 19, scale = 8)
    private BigDecimal actualPnl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FillType fillType;

    @OneToMany(mappedBy = "sell", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TradeMatch> matchedBuys = new ArrayList<>();

    @OneToMany(mappedBy = "buy", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TradeMatch> matchedSells = new ArrayList<>();
}
