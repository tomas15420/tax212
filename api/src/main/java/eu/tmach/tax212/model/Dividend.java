package eu.tmach.tax212.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dividends", indexes = {
        @Index(name = "idx_div_ticker", columnList = "ticker"),
        @Index(name = "idx_paid_on", columnList = "paidOn"),
        @Index(name = "idx_t212id", columnList = "t212id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dividend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String t212id;

    @Column(nullable = false, length = 30)
    private String ticker;

    @Column(nullable = false)
    private LocalDateTime paidOn;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal amountInEuro;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal grossAmountPerShare;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private DividendType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_id", nullable = false)
    private Instrument instrument;
}
