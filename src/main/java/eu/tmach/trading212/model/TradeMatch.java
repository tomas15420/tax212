package eu.tmach.trading212.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "trade_matches")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sell_id", nullable = false)
    private Transaction sell;

    @ManyToOne
    @JoinColumn(name = "buy_id", nullable = false)
    private Transaction buy;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal matchedQuantity;
}
