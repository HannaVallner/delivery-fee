package ee.fujitsu.delivery_fee.repository;

import ee.fujitsu.delivery_fee.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long>{

    WeatherObservation findFirstByStationNameOrderByObservedAtDesc(String stationName);

    WeatherObservation findFirstByStationNameAndObservedAtLessThanEqualOrderByObservedAtDesc(
            String stationName, Instant requestedAt);
}
