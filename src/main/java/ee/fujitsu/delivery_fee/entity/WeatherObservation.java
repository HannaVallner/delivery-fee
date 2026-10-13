package ee.fujitsu.delivery_fee.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="weather_observations")
public class WeatherObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="timestamp", nullable=false)
    private LocalDateTime timestamp;

    @Column(name="station_name",nullable=false)
    private String stationName;

    @Column(name="wmo_code", nullable=false)
    private String wmoCode;

    @Column(name="airTemperature", nullable=false)
    private BigDecimal airTemperature;

    @Column(name="wind_speed", nullable=false)
    private BigDecimal windSpeed;

    @Column(name="weather_phenomenon", nullable=false)
    private String weatherPhenomenon;
}
