package ee.fujitsu.delivery_fee.controller;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
        if (baseFee.getCity() == null || !SUPPORTED_CITIES.contains(baseFee.getCity())) {
            throw new IllegalArgumentException(
                    "Unsupported city. Supported cities are: " + SUPPORTED_CITIES);
        }

        if (baseFee.getBaseFee() == null
                || baseFee.getBaseFee().signum() < 0) {
            throw new IllegalArgumentException(
                    "Base fee must be zero or greater");
        }

        if (baseFee.getVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        if (regionalBaseFeeRepository.existsByCityAndVehicleType(
                baseFee.getCity(), baseFee.getVehicleType())) {
            throw new IllegalArgumentException(
                    "A base fee already exists for this city and vehicle type");
        }

        return regionalBaseFeeRepository.save(baseFee);
    }

    @PutMapping("/api/base-fees/{id}")
    public RegionalBaseFee updateBaseFee(
            @PathVariable Long id,
            @RequestBody RegionalBaseFee updatedFee) {

        Optional<RegionalBaseFee> optionalFee = regionalBaseFeeRepository.findById(id);
        if (optionalFee.isEmpty()) {
            throw new IllegalArgumentException("Base fee not found with id: " + id);
        }
        RegionalBaseFee existingFee = optionalFee.get();

        if (updatedFee.getCity() == null || !SUPPORTED_CITIES.contains(updatedFee.getCity())) {
            throw new IllegalArgumentException(
                    "Unsupported city. Supported cities are: " + SUPPORTED_CITIES);
        }

        if (updatedFee.getBaseFee() == null
                || updatedFee.getBaseFee().signum() < 0) {
            throw new IllegalArgumentException(
                    "Base fee must be zero or greater");
        }

        if (updatedFee.getVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        boolean duplicateExists = regionalBaseFeeRepository.existsByCityAndVehicleType(
                                    updatedFee.getCity(), updatedFee.getVehicleType());

        if (duplicateExists && !(existingFee.getCity().equals(updatedFee.getCity())
                && existingFee.getVehicleType() == updatedFee.getVehicleType())) {
            throw new IllegalArgumentException(
                    "A base fee already exists for this city and vehicle type");
        }

        existingFee.setCity(updatedFee.getCity());
        existingFee.setVehicleType(updatedFee.getVehicleType());
        existingFee.setBaseFee(updatedFee.getBaseFee());

        return regionalBaseFeeRepository.save(existingFee);
    }

    @DeleteMapping("/api/base-fees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBaseFee(@PathVariable Long id) {
        if (!regionalBaseFeeRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Base fee with id: " + id + " not found");
        }

        regionalBaseFeeRepository.deleteById(id);
    }
}
