package ee.fujitsu.delivery_fee.repository;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {

    boolean existsByCityAndVehicleType(String city, VehicleType vehicleType);
    RegionalBaseFee findByCityAndVehicleType(String city, VehicleType vehicleType);
    List<RegionalBaseFee> findAllByCity(String city);
    List<RegionalBaseFee> findAllByVehicleType(VehicleType vehicleType);
}
