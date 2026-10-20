package ee.fujitsu.delivery_fee.controller;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegionalBaseFeeController {

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;

    public RegionalBaseFeeController(RegionalBaseFeeRepository regionalBaseFeeRepository) {
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
    }

    @GetMapping("/api/base-fees")
    public List<RegionalBaseFee> getBaseFees(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) VehicleType vehicleType) {

        // If both city and vehicle type are given, find results for both
        if (city != null && vehicleType != null) {
            RegionalBaseFee baseFee = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicleType);
            // Wrap result in list
            return baseFee == null ? List.of() : List.of(baseFee);
        }

        if (vehicleType != null) {
            return regionalBaseFeeRepository.findAllByVehicleType(vehicleType);
        }

        if (city != null) {
            return regionalBaseFeeRepository.findAllByCity(city);
        }

        return regionalBaseFeeRepository.findAll();
    }
}
