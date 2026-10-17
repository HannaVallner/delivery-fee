package ee.fujitsu.delivery_fee.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="weather_observations")
public class WeatherObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="observed_at", nullable=false)
    private Instant observedAt;

    @Column(name="station_name",nullable=false)
    private String stationName;

    @Column(name="wmo_code")
    private String wmoCode;

    @Column(name="air_temperature")
    private BigDecimal airTemperature;

    @Column(name="wind_speed")
    private BigDecimal windSpeed;

    @Column(name="weather_phenomenon")
    private String weatherPhenomenon;
}
