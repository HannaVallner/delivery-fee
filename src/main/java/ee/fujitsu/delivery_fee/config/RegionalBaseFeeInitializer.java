package ee.fujitsu.delivery_fee.config;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
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
            addFee(repository, "Tallinn", "car", "4.00");
            addFee(repository, "Tallinn", "scooter", "3.50");
            addFee(repository, "Tallinn", "bike", "3.00");

            addFee(repository, "Tartu", "car", "3.50");
            addFee(repository, "Tartu", "scooter", "3.00");
            addFee(repository, "Tartu", "bike", "2.50");

            addFee(repository, "Pärnu", "car", "3.00");
            addFee(repository, "Pärnu", "scooter", "2.50");
            addFee(repository, "Pärnu", "bike", "2.00");
        };
    }

    private void addFee(RegionalBaseFeeRepository repository, String city, String vehicleType, String fee) {
        if (!repository.existsByCityAndVehicleType(city, vehicleType)) {
            RegionalBaseFee baseFee = new RegionalBaseFee();
            baseFee.setCity(city);
            baseFee.setVehicleType(vehicleType);
            baseFee.setBaseFee(new BigDecimal(fee));

            repository.save(baseFee);
        }
    }
}
