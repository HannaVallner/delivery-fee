package ee.fujitsu.delivery_fee.repository;

import ee.fujitsu.delivery_fee.entity.WeatherObservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherObservationRepository extends JpaRepository<WeatherObservation, Long>{
}
