package ee.fujitsu.delivery_fee.controller;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class RegionalBaseFeeController {

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;
    private static final Set<String> SUPPORTED_CITIES = Set.of("Tallinn", "Tartu", "Pärnu");

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

    @PostMapping("/api/base-fees")
    @ResponseStatus(HttpStatus.CREATED)
    public RegionalBaseFee createBaseFee(@RequestBody RegionalBaseFee baseFee) {
        if (!SUPPORTED_CITIES.contains(baseFee.getCity())) {
            throw new IllegalArgumentException(
                    "Unsupported city. Supported cities are: " + SUPPORTED_CITIES);
        }

        if (baseFee.getBaseFee() == null
                || baseFee.getBaseFee().signum() < 0) {
            throw new IllegalArgumentException(
                    "Base fee must be zero or greater");
        }

        if (regionalBaseFeeRepository.existsByCityAndVehicleType(
                baseFee.getCity(), baseFee.getVehicleType())) {
            throw new IllegalArgumentException(
                    "A base fee already exists for this city and vehicle type");
        }

        return regionalBaseFeeRepository.save(baseFee);
    }

}
