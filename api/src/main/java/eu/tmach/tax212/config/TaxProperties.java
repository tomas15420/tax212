package eu.tmach.tax212.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tax.limits")
@Getter
@Setter
public class TaxProperties {
    private Integer assetSaleAnnualCap;
    private Integer incidentalIncomeCap;
    private Integer holdingPeriodYears;
}
