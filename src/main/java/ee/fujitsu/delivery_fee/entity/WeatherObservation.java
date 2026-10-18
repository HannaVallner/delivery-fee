package ee.fujitsu.delivery_fee.entity;

import jakarta.persistence.*;

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
    private Double airTemperature;

    @Column(name="wind_speed")
    private Double windSpeed;

    @Column(name="weather_phenomenon")
    private String weatherPhenomenon;


    // Getters for all fields
    public Long getId() {
        return id;
    }

    public Instant getObservedAt() {
        return observedAt;
    }

    public String getStationName() {
        return stationName;
    }

    public String getWmoCode() {
        return wmoCode;
    }

    public Double getAirTemperature() {
        return airTemperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    // Setters for all fields aside from id, as it is generated as needs automatically

    public void setObservedAt(Instant observedAt) {
        this.observedAt = observedAt;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setWmoCode(String wmoCode) {
        this.wmoCode = wmoCode;
    }

    public void setAirTemperature(Double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWeatherPhenomenon(String weatherPhenomenon) {
        this.weatherPhenomenon = weatherPhenomenon;
    }
}
