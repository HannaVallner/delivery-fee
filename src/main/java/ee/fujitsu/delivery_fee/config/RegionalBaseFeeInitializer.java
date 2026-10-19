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
            addFee(repository, "Tallinn", VehicleType.CAR, new BigDecimal("4.00"));
            addFee(repository, "Tallinn", VehicleType.SCOOTER, new BigDecimal("3.50"));
            addFee(repository, "Tallinn", VehicleType.BIKE, new BigDecimal("3.00"));

            addFee(repository, "Tartu", VehicleType.CAR, new BigDecimal("3.50"));
            addFee(repository, "Tartu", VehicleType.SCOOTER, new BigDecimal("3.00"));
            addFee(repository, "Tartu", VehicleType.BIKE, new BigDecimal("2.50"));

            addFee(repository, "Pärnu", VehicleType.CAR, new BigDecimal("3.00"));
            addFee(repository, "Pärnu", VehicleType.SCOOTER, new BigDecimal("2.50"));
            addFee(repository, "Pärnu", VehicleType.BIKE, new BigDecimal("2.00"));
        };
    }

    private void addFee(RegionalBaseFeeRepository repository, String city,
                        VehicleType vehicleType, BigDecimal fee) {
        if (!repository.existsByCityAndVehicleType(city, vehicleType)) {
            RegionalBaseFee baseFee = new RegionalBaseFee();
            baseFee.setCity(city);
            baseFee.setVehicleType(vehicleType);
            baseFee.setBaseFee(fee);

            repository.save(baseFee);
        }
    }
}
