package ee.fujitsu.delivery_fee.config;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.math.BigDecimal;

@Configuration
public class RegionalBaseFeeInitializer {

    @Bean
    CommandLineRunner initializeRegionalBaseFees(RegionalBaseFeeRepository repository) {
        return args -> {
            addFee(repository, "Tallinn", VehicleType.CAR, "4.00");
            addFee(repository, "Tallinn", VehicleType.SCOOTER, "3.50");
            addFee(repository, "Tallinn", VehicleType.BIKE, "3.00");

            addFee(repository, "Tartu", VehicleType.CAR, "3.50");
            addFee(repository, "Tartu", VehicleType.SCOOTER, "3.00");
            addFee(repository, "Tartu", VehicleType.BIKE, "2.50");

            addFee(repository, "Pärnu", VehicleType.CAR, "3.00");
            addFee(repository, "Pärnu", VehicleType.SCOOTER, "2.50");
            addFee(repository, "Pärnu", VehicleType.BIKE, "2.00");
        };
    }

    private void addFee(RegionalBaseFeeRepository repository, String city, VehicleType vehicleType, String fee) {
        if (!repository.existsByCityAndVehicleType(city, vehicleType)) {
            RegionalBaseFee baseFee = new RegionalBaseFee();
            baseFee.setCity(city);
            baseFee.setVehicleType(vehicleType);
            baseFee.setBaseFee(new BigDecimal(fee));

            repository.save(baseFee);
        }
    }
}
