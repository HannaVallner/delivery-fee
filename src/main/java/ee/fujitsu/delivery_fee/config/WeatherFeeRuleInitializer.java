package ee.fujitsu.delivery_fee.config;

import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.entity.WeatherCondition;
import ee.fujitsu.delivery_fee.entity.WeatherFeeRule;
import ee.fujitsu.delivery_fee.entity.WeatherRuleType;
import ee.fujitsu.delivery_fee.repository.WeatherFeeRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class WeatherFeeRuleInitializer {

    // Add weather fee rules as per business rules
    @Bean
    CommandLineRunner initializeWeatherFeeRules(WeatherFeeRuleRepository repository) {
        return args -> {
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.SURCHARGE,
                    WeatherCondition.TEMPERATURE, null, -10.0, new BigDecimal("1.00"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                    WeatherCondition.TEMPERATURE, null, -10.0, new BigDecimal("1.00"));
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.SURCHARGE,
                    WeatherCondition.TEMPERATURE, -10.0, 0.0, new BigDecimal("0.50"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                    WeatherCondition.TEMPERATURE,-10.0, 0.0, new BigDecimal("0.50"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                    WeatherCondition.WIND,10.0, 20.0, new BigDecimal("0.50"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.WIND,20.0, null, null);
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.SURCHARGE,
                    WeatherCondition.SNOW, null, null, new BigDecimal("1.00"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                    WeatherCondition.SNOW, null, null, new BigDecimal("1.00"));
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.SURCHARGE,
                    WeatherCondition.SLEET, null, null, new BigDecimal("1.00"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                    WeatherCondition.SLEET, null, null, new BigDecimal("1.00"));
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.SURCHARGE,
                    WeatherCondition.RAIN, null, null, new BigDecimal("0.50"));
            addRule(repository, VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                    WeatherCondition.RAIN, null, null, new BigDecimal("0.50"));

            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.GLAZE, null, null, null);
            addRule(repository, VehicleType.BIKE, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.GLAZE, null, null, null);
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.HAIL, null, null, null);
            addRule(repository, VehicleType.BIKE, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.HAIL, null, null, null);
            addRule(repository, VehicleType.SCOOTER, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.THUNDER, null, null, null);
            addRule(repository, VehicleType.BIKE, WeatherRuleType.FORBIDDEN,
                    WeatherCondition.THUNDER, null, null, null);
        };
    }

    private void addRule(WeatherFeeRuleRepository repository, VehicleType vehicleType,
                         WeatherRuleType ruleType, WeatherCondition weatherCondition,
                         Double minValue, Double maxValue, BigDecimal fee) {
        if (!repository.existsByVehicleTypeAndRuleTypeAndWeatherConditionAndMinValueAndMaxValue(
                vehicleType, ruleType, weatherCondition, minValue, maxValue)) {

            WeatherFeeRule rule = new WeatherFeeRule();
            rule.setVehicleType(vehicleType);
            rule.setRuleType(ruleType);
            rule.setWeatherCondition(weatherCondition);
            rule.setMinValue(minValue);
            rule.setMaxValue(maxValue);
            rule.setAdditionalFee(fee);

            repository.save(rule);
        }
    }
}
