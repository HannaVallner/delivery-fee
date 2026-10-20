package ee.fujitsu.delivery_fee.service;

import ee.fujitsu.delivery_fee.entity.*;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import ee.fujitsu.delivery_fee.repository.WeatherFeeRuleRepository;
import ee.fujitsu.delivery_fee.repository.WeatherObservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryFeeServiceTest {

    private RegionalBaseFeeRepository regionalBaseFeeRepository;
    private WeatherObservationRepository weatherObservationRepository;
    private WeatherFeeRuleRepository weatherFeeRuleRepository;
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    void setUp() {
        regionalBaseFeeRepository = mock(RegionalBaseFeeRepository.class);
        weatherObservationRepository = mock(WeatherObservationRepository.class);
        weatherFeeRuleRepository = mock(WeatherFeeRuleRepository.class);

        deliveryFeeService = new DeliveryFeeService(
                regionalBaseFeeRepository,
                weatherObservationRepository,
                weatherFeeRuleRepository);
    }

    @Test
    void bikeGetsOneEuroSurchargeBelowMinusTenDegrees() {
        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(-11.0);

        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setVehicleType(VehicleType.BIKE);
        rule.setRuleType(WeatherRuleType.SURCHARGE);
        rule.setWeatherCondition(WeatherCondition.TEMPERATURE);
        rule.setMinValue(null);
        rule.setMaxValue(-10.0);
        rule.setAdditionalFee(new BigDecimal("1.00"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateWeatherFee(
                observation, VehicleType.BIKE);

        assertEquals(new BigDecimal("1.00"), result);
    }

    @Test
    void bikeGetsFiftyCentSurchargeAtMinusTenDegrees() {
        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(-10.0);

        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setVehicleType(VehicleType.BIKE);
        rule.setRuleType(WeatherRuleType.SURCHARGE);
        rule.setWeatherCondition(WeatherCondition.TEMPERATURE);
        rule.setMinValue(-10.0);
        rule.setMaxValue(0.0);
        rule.setAdditionalFee(new BigDecimal("0.50"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateWeatherFee(
                observation, VehicleType.BIKE);

        assertEquals(new BigDecimal("0.50"), result);
    }

    @Test
    void bikeGetsFiftyCentSurchargeAtZeroDegrees() {
        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(0.0);

        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setVehicleType(VehicleType.BIKE);
        rule.setRuleType(WeatherRuleType.SURCHARGE);
        rule.setWeatherCondition(WeatherCondition.TEMPERATURE);
        rule.setMinValue(-10.0);
        rule.setMaxValue(0.0);
        rule.setAdditionalFee(new BigDecimal("0.50"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateWeatherFee(
                observation, VehicleType.BIKE);

        assertEquals(new BigDecimal("0.50"), result);
    }

    @Test
    void missingTemperatureDoesNotTriggerSurcharge() {
        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(null);

        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setVehicleType(VehicleType.BIKE);
        rule.setRuleType(WeatherRuleType.SURCHARGE);
        rule.setWeatherCondition(WeatherCondition.TEMPERATURE);
        rule.setMinValue(-10.0);
        rule.setMaxValue(0.0);
        rule.setAdditionalFee(new BigDecimal("0.50"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateWeatherFee(
                observation, VehicleType.BIKE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void bikeGetsFiftyCentSurchargeAtTenWindSpeed() {
        WeatherObservation observation = new WeatherObservation();
        observation.setWindSpeed(10.0);

        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setVehicleType(VehicleType.BIKE);
        rule.setRuleType(WeatherRuleType.SURCHARGE);
        rule.setWeatherCondition(WeatherCondition.WIND);
        rule.setMinValue(10.0);
        rule.setMaxValue(20.0);
        rule.setAdditionalFee(new BigDecimal("0.50"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateWeatherFee(
                observation, VehicleType.BIKE);

        assertEquals(new BigDecimal("0.50"), result);
    }

    @Test
    void bikeGetsFiftyCentSurchargeAtTwentyWindSpeed() {
        WeatherObservation observation = new WeatherObservation();
        observation.setWindSpeed(20.0);

        WeatherFeeRule rule = createRule(
                VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                WeatherCondition.WIND, 10.0, 20.0,
                new BigDecimal("0.50"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateWeatherFee(
                observation, VehicleType.BIKE);

        assertEquals(new BigDecimal("0.50"), result);
    }

    @Test
    void bikeDeliveryIsForbiddenAboveTwentyWindSpeed() {
        WeatherObservation observation = new WeatherObservation();
        observation.setWindSpeed(20.1);

        WeatherFeeRule rule = createRule(
                VehicleType.BIKE, WeatherRuleType.FORBIDDEN,
                WeatherCondition.WIND, 20.0, null, null);

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        assertThrows(IllegalArgumentException.class,
                () -> deliveryFeeService.calculateWeatherFee(
                        observation, VehicleType.BIKE));
    }

    @Test
    void bikeGetsOneEuroSurchargeInSnow() {
        WeatherObservation observation = new WeatherObservation();
        observation.setWeatherPhenomenon("Light snow");

        WeatherFeeRule rule = createRule(
                VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                WeatherCondition.SNOW, null, null,
                new BigDecimal("1.00"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        assertEquals(new BigDecimal("1.00"),
                deliveryFeeService.calculateWeatherFee(
                        observation, VehicleType.BIKE));
    }

    @Test
    void bikeGetsFiftyCentSurchargeInRain() {
        WeatherObservation observation = new WeatherObservation();
        observation.setWeatherPhenomenon("Light rain");

        WeatherFeeRule rule = createRule(
                VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                WeatherCondition.RAIN, null, null,
                new BigDecimal("0.50"));

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        assertEquals(new BigDecimal("0.50"),
                deliveryFeeService.calculateWeatherFee(
                        observation, VehicleType.BIKE));
    }

    @Test
    void bikeDeliveryIsForbiddenInGlaze() {
        WeatherObservation observation = new WeatherObservation();
        observation.setWeatherPhenomenon("Glaze");

        WeatherFeeRule rule = createRule(
                VehicleType.BIKE, WeatherRuleType.FORBIDDEN,
                WeatherCondition.GLAZE, null, null, null);

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        assertThrows(IllegalArgumentException.class,
                () -> deliveryFeeService.calculateWeatherFee(
                        observation, VehicleType.BIKE));
    }

    @Test
    void carHasNoWeatherFeeWhenNoCarRulesExist() {
        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(-20.0);
        observation.setWindSpeed(30.0);
        observation.setWeatherPhenomenon("Snow and thunder");

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.CAR))
                .thenReturn(java.util.List.of());

        assertEquals(BigDecimal.ZERO,
                deliveryFeeService.calculateWeatherFee(
                        observation, VehicleType.CAR));
    }

    @Test
    void deliveryFeeCombinesBaseFeeAndWeatherSurcharge() {
        RegionalBaseFee baseFee = new RegionalBaseFee();
        baseFee.setBaseFee(new BigDecimal("3.00"));

        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(-5.0);

        WeatherFeeRule rule = createRule(
                VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                WeatherCondition.TEMPERATURE, -10.0, 0.0,
                new BigDecimal("0.50"));

        when(regionalBaseFeeRepository.findByCityAndVehicleType(
                "Tallinn", VehicleType.BIKE)).thenReturn(baseFee);

        when(weatherObservationRepository
                .findFirstByStationNameOrderByObservedAtDesc("Tallinn-Harku"))
                .thenReturn(observation);

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(rule));

        BigDecimal result = deliveryFeeService.calculateDeliveryFee(
                "Tallinn", VehicleType.BIKE, null);

        assertEquals(new BigDecimal("3.50"), result);
    }

    @Test
    void deliveryFeeCombinesMultipleWeatherSurcharges() {
        RegionalBaseFee baseFee = new RegionalBaseFee();
        baseFee.setBaseFee(new BigDecimal("3.00"));

        WeatherObservation observation = new WeatherObservation();
        observation.setAirTemperature(-12.0);
        observation.setWindSpeed(15.0);

        WeatherFeeRule temperatureRule = createRule(
                VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                WeatherCondition.TEMPERATURE, null, -10.0,
                new BigDecimal("1.00"));

        WeatherFeeRule windRule = createRule(
                VehicleType.BIKE, WeatherRuleType.SURCHARGE,
                WeatherCondition.WIND, 10.0, 20.0,
                new BigDecimal("0.50"));

        when(regionalBaseFeeRepository.findByCityAndVehicleType(
                "Tallinn", VehicleType.BIKE)).thenReturn(baseFee);

        when(weatherObservationRepository
                .findFirstByStationNameOrderByObservedAtDesc("Tallinn-Harku"))
                .thenReturn(observation);

        when(weatherFeeRuleRepository.findByVehicleType(VehicleType.BIKE))
                .thenReturn(java.util.List.of(temperatureRule, windRule));

        BigDecimal result = deliveryFeeService.calculateDeliveryFee(
                "Tallinn", VehicleType.BIKE, null);

        assertEquals(new BigDecimal("4.50"), result);
    }

    @Test
    void historicalCalculationFailsWhenNoObservationExists() {
        java.time.Instant requestedAt =
                java.time.Instant.parse("2020-01-01T00:00:00Z");

        when(weatherObservationRepository
                .findFirstByStationNameAndObservedAtLessThanEqualOrderByObservedAtDesc(
                        "Tallinn-Harku", requestedAt))
                .thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> deliveryFeeService.getWeatherObservation(
                        "Tallinn", requestedAt));
    }

    private WeatherFeeRule createRule(
            VehicleType vehicleType,
            WeatherRuleType ruleType,
            WeatherCondition condition,
            Double minValue,
            Double maxValue,
            BigDecimal additionalFee) {

        WeatherFeeRule rule = new WeatherFeeRule();
        rule.setVehicleType(vehicleType);
        rule.setRuleType(ruleType);
        rule.setWeatherCondition(condition);
        rule.setMinValue(minValue);
        rule.setMaxValue(maxValue);
        rule.setAdditionalFee(additionalFee);
        return rule;
    }

}