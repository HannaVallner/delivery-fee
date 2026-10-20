package ee.fujitsu.delivery_fee.service;

import ee.fujitsu.delivery_fee.entity.*;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import ee.fujitsu.delivery_fee.repository.WeatherFeeRuleRepository;
import ee.fujitsu.delivery_fee.repository.WeatherObservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DeliveryFeeService {

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;
    private final WeatherObservationRepository weatherObservationRepository;
    private final WeatherFeeRuleRepository weatherFeeRuleRepository;

    public DeliveryFeeService(RegionalBaseFeeRepository regionalBaseFeeRepository,
                              WeatherObservationRepository weatherObservationRepository,
                              WeatherFeeRuleRepository weatherFeeRuleRepository) {
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
        this.weatherObservationRepository = weatherObservationRepository;
        this.weatherFeeRuleRepository = weatherFeeRuleRepository;
    }

    public BigDecimal getBaseFee(String city, VehicleType vehicleType) {
        RegionalBaseFee baseFee = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicleType);

        if (baseFee == null) {
            throw new IllegalArgumentException("No base fee found for the city of " + city
                    + " for vehicle type " + vehicleType);
        }
        return baseFee.getBaseFee();
    }

    public WeatherObservation getLatestWeatherObservation(String city) {
        String stationName = switch (city) {
            case "Tallinn" -> "Tallinn-Harku";
            case "Tartu" -> "Tartu-Tõravere";
            case "Pärnu" -> "Pärnu";
            default -> throw new IllegalArgumentException("Unsupported city: " + city);
        };

        WeatherObservation weatherObservation =
                weatherObservationRepository.findFirstByStationNameOrderByObservedAtDesc(stationName);

        if (weatherObservation == null) {
            throw new IllegalArgumentException("No weather observation found for " + city);
        }
        return weatherObservation;
    }

    private boolean isValueInRange(Double targetValue, Double minValue, Double maxValue) {
        if (targetValue == null) {
            return false;
        }
        // If min value is null, check if target value is strictly smaller than the max value
        if (minValue == null) {
            return maxValue != null && targetValue < maxValue;
        }
        // If max value is null, check if target value is strictly larger than the min value
        if (maxValue == null) {
            return targetValue > minValue;
        }
        // If both max and min values exists, check if target value is inclusively in range
        return targetValue >= minValue && targetValue <= maxValue;
    }

    private boolean isRuleApplicable(WeatherFeeRule rule, WeatherObservation observation) {
        return switch (rule.getWeatherCondition()) {
            // Observed temperature between target rule parameters
            case TEMPERATURE -> isValueInRange(observation.getAirTemperature(),
                    rule.getMinValue(), rule.getMaxValue());
            case WIND -> isValueInRange(observation.getWindSpeed(),
                    rule.getMinValue(), rule.getMaxValue());
            case RAIN -> observation.getWeatherPhenomenon() != null
                    && observation.getWeatherPhenomenon().toLowerCase().contains("rain");
            case HAIL -> observation.getWeatherPhenomenon() != null
                    && observation.getWeatherPhenomenon().toLowerCase().contains("hail");
            case SNOW -> observation.getWeatherPhenomenon() != null
                    && observation.getWeatherPhenomenon().toLowerCase().contains("snow");
            case SLEET -> observation.getWeatherPhenomenon() != null
                    && observation.getWeatherPhenomenon().toLowerCase().contains("sleet");
            case GLAZE -> observation.getWeatherPhenomenon() != null
                    && observation.getWeatherPhenomenon().toLowerCase().contains("glaze");
            case THUNDER -> observation.getWeatherPhenomenon() != null
                    && observation.getWeatherPhenomenon().toLowerCase().contains("thunder");
        };
    }

    // Calculate extra fee based on weather conditions and vehicle type
    public BigDecimal calculateWeatherFee(WeatherObservation weatherObservation,
                                          VehicleType vehicleType) {
        // Find all rules for selected vehicle type
        List<WeatherFeeRule> rules = weatherFeeRuleRepository.findByVehicleType(vehicleType);
        // Initialize additional weather fee at zero
        BigDecimal weatherFee = BigDecimal.ZERO;
        for (WeatherFeeRule rule : rules) {
            // Check whether a rule is applicable in target weather conditions
            if (isRuleApplicable(rule, weatherObservation)) {
                // If the rule forbids delivery with target vehicle type and weather,
                // quit calculating and throw error
                if (rule.getRuleType() == WeatherRuleType.FORBIDDEN) {
                    throw new IllegalArgumentException("Usage of selected vehicle type is forbidden");
                }
                weatherFee = weatherFee.add(rule.getAdditionalFee());
            }
        }
        return weatherFee;
    }

    public BigDecimal calculateDeliveryFee(String city, VehicleType vehicleType) {
        BigDecimal baseFee = getBaseFee(city, vehicleType);
        WeatherObservation weatherObservation = getLatestWeatherObservation(city);
        BigDecimal weatherFee = calculateWeatherFee(weatherObservation, vehicleType);
        return baseFee.add(weatherFee);
    }

}
