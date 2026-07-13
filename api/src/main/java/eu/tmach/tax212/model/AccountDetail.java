package eu.tmach.tax212.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_details")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDetail {
    @Id
    @Column(name = "detail_key")
    private String key;

    @Column(name = "detail_value", nullable = false)
    private String value;
}
