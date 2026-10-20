package ee.fujitsu.delivery_fee.repository;

import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.entity.WeatherCondition;
import ee.fujitsu.delivery_fee.entity.WeatherFeeRule;
import ee.fujitsu.delivery_fee.entity.WeatherRuleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherFeeRuleRepository extends JpaRepository<WeatherFeeRule, Long> {
    boolean existsByVehicleTypeAndRuleTypeAndWeatherConditionAndMinValueAndMaxValue(
            VehicleType vehicleType, WeatherRuleType ruleType,
            WeatherCondition weatherCondition,
            Double minValue, Double maxValue);

}
