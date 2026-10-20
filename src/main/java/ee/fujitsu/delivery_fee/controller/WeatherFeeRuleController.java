package ee.fujitsu.delivery_fee.controller;

import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.entity.WeatherFeeRule;
import ee.fujitsu.delivery_fee.entity.WeatherRuleType;
import ee.fujitsu.delivery_fee.repository.WeatherFeeRuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class WeatherFeeRuleController {

    private final WeatherFeeRuleRepository weatherFeeRuleRepository;

    public WeatherFeeRuleController(WeatherFeeRuleRepository weatherFeeRuleRepository) {
        this.weatherFeeRuleRepository = weatherFeeRuleRepository;
    }

    @GetMapping("/api/weather-rules")
    public List<WeatherFeeRule> getWeatherRules(
            @RequestParam(required = false) VehicleType vehicleType) {

        if (vehicleType != null) {
            return weatherFeeRuleRepository.findByVehicleType(vehicleType);
        }

        return weatherFeeRuleRepository.findAll();
    }

    @PostMapping("/api/weather-rules")
    @ResponseStatus(HttpStatus.CREATED)
    public WeatherFeeRule createWeatherRule(
            @RequestBody WeatherFeeRule rule) {

        if (rule.getVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        if (rule.getRuleType() == null) {
            throw new IllegalArgumentException("Rule type is required");
        }

        if (rule.getWeatherCondition() == null) {
            throw new IllegalArgumentException("Weather condition is required");
        }

        if (rule.getMinValue() != null && rule.getMaxValue() != null
                && rule.getMinValue() > rule.getMaxValue()) {
            throw new IllegalArgumentException(
                    "Minimum value cannot be greater than maximum value");
        }

        if (rule.getRuleType() == ee.fujitsu.delivery_fee.entity.WeatherRuleType.SURCHARGE) {
            if (rule.getAdditionalFee() == null
                    || rule.getAdditionalFee().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Surcharge must have an additional fee greater than zero");
            }
        }
        else if (rule.getAdditionalFee() != null) {
            throw new IllegalArgumentException(
                    "Forbidden rules must not have an additional fee");
        }

        if (weatherFeeRuleRepository
                .existsByVehicleTypeAndRuleTypeAndWeatherConditionAndMinValueAndMaxValue(
                        rule.getVehicleType(),
                        rule.getRuleType(),
                        rule.getWeatherCondition(),
                        rule.getMinValue(),
                        rule.getMaxValue())) {
            throw new IllegalArgumentException("This weather rule already exists");
        }

        return weatherFeeRuleRepository.save(rule);
    }

    @PutMapping("/api/weather-rules/{id}")
    public WeatherFeeRule updateWeatherRule(
            @PathVariable Long id,
            @RequestBody WeatherFeeRule updatedRule) {

        WeatherFeeRule existingRule = weatherFeeRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Weather rule not found with id: " + id));

        if (updatedRule.getVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        if (updatedRule.getRuleType() == null) {
            throw new IllegalArgumentException("Rule type is required");
        }

        if (updatedRule.getWeatherCondition() == null) {
            throw new IllegalArgumentException("Weather condition is required");
        }

        if (updatedRule.getMinValue() != null
                && updatedRule.getMaxValue() != null
                && updatedRule.getMinValue() > updatedRule.getMaxValue()) {
            throw new IllegalArgumentException(
                    "Minimum value cannot be greater than maximum value");
        }

        if (updatedRule.getRuleType() == WeatherRuleType.SURCHARGE) {
            if (updatedRule.getAdditionalFee() == null
                    || updatedRule.getAdditionalFee().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Surcharge must have an additional fee of zero or greater");
            }
        }
        else if (updatedRule.getAdditionalFee() != null) {
            throw new IllegalArgumentException(
                    "Forbidden rules must not have an additional fee");
        }

        boolean duplicateExists =
                weatherFeeRuleRepository
                        .existsByVehicleTypeAndRuleTypeAndWeatherConditionAndMinValueAndMaxValue(
                                updatedRule.getVehicleType(),
                                updatedRule.getRuleType(),
                                updatedRule.getWeatherCondition(),
                                updatedRule.getMinValue(),
                                updatedRule.getMaxValue());

        boolean sameRule = existingRule.getVehicleType() == updatedRule.getVehicleType()
                && existingRule.getRuleType() == updatedRule.getRuleType()
                && existingRule.getWeatherCondition() == updatedRule.getWeatherCondition()
                && java.util.Objects.equals(existingRule.getMinValue(), updatedRule.getMinValue())
                && java.util.Objects.equals(existingRule.getMaxValue(), updatedRule.getMaxValue());

        if (duplicateExists && !sameRule) {
            throw new IllegalArgumentException("This weather rule already exists");
        }

        existingRule.setVehicleType(updatedRule.getVehicleType());
        existingRule.setRuleType(updatedRule.getRuleType());
        existingRule.setWeatherCondition(updatedRule.getWeatherCondition());
        existingRule.setMinValue(updatedRule.getMinValue());
        existingRule.setMaxValue(updatedRule.getMaxValue());
        existingRule.setAdditionalFee(updatedRule.getAdditionalFee());

        return weatherFeeRuleRepository.save(existingRule);
    }

    @DeleteMapping("/api/weather-rules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeatherRule(@PathVariable Long id) {
        if (!weatherFeeRuleRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Weather rule not found with id: " + id);
        }

        weatherFeeRuleRepository.deleteById(id);
    }

}
