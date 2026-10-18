package ee.fujitsu.delivery_fee.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name="regional_base_fees",
        // Disallow for same city-vehicle duplicates
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"city", "vehicle_type"})
        })
public class RegionalBaseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="city", nullable=false)
    private String city;

    @Column(name="vehicle_type", nullable=false)
    private String vehicleType;

    // Monetary values in  bigdecimals for accuracy,
    // up to 10 numerical values in a fee, up to 2 decimal points
    @Column(name="base_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFee;

    public RegionalBaseFee() {

    }

    // Setters for all fields aside from id, as it is generated as needed automatically
    public void setCity(String city) {
        this.city = city;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    // Getters for all fields
    public Long getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }
}
