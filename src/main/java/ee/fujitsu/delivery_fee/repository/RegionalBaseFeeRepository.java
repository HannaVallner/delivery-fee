package ee.fujitsu.delivery_fee.repository;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {
    boolean existsByCityAndVehicleType(String city, String vehicleType);
}
