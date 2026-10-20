package ee.fujitsu.delivery_fee.service;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DeliveryFeeService {

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    public DeliveryFeeService(RegionalBaseFeeRepository regionalBaseFeeRepository) {
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
    }

    public BigDecimal getBaseFee(String city, VehicleType vehicleType) {
        RegionalBaseFee baseFee = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicleType);

        if (baseFee == null) {
            throw new IllegalArgumentException("No base fee found for the city of" + city
                    + " for vehicle tyoe" + vehicleType);
        }
        return baseFee.getBaseFee();
    }
}
