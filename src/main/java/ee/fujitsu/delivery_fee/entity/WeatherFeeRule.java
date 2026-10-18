package ee.fujitsu.delivery_fee.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="weather_fee_rule")
public class WeatherFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="vehicle_type", nullable = false)
    private String vehicleType;

    // Defines whether a rule forbids delivery or adds surcharge
    @Column(name="rule_type", nullable = false)
    private String ruleType;

    @Column(name="weather_condition", nullable = false)
    private String weatherCondition;

    // Minimum value for a given weather condition to result in a given fee
    @Column(name="min_value")
    private Double minValue;

    // Maximum value for a given weather condition to result in a given fee
    @Column(name="max_value")
    private Double maxValue;

    // Monetary values in  bigdecimals for accuracy,
    // up to 10 numerical values in a fee, up to 2 decimal points
    // nullable to allow for forbidden delivery rules without surcharge
    @Column(name="additional_fee", precision = 10, scale = 2)
    private BigDecimal additionalFee;

    // Getters for all fields
    public Long getId() {
        return id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getRuleType() {
        return ruleType;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public BigDecimal getAdditionalFee() {
        return additionalFee;
    }

    // Setters for all fields aside from id, as it is generated as needs automatically

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public void setAdditionalFee(BigDecimal additionalFee) {
        this.additionalFee = additionalFee;
    }
}
