package ee.fujitsu.delivery_fee.service;

import ee.fujitsu.delivery_fee.entity.RegionalBaseFee;
import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.entity.WeatherObservation;
import ee.fujitsu.delivery_fee.repository.RegionalBaseFeeRepository;
import ee.fujitsu.delivery_fee.repository.WeatherObservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DeliveryFeeService {

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;
    private final WeatherObservationRepository weatherObservationRepository;

    public DeliveryFeeService(RegionalBaseFeeRepository regionalBaseFeeRepository,
                              WeatherObservationRepository weatherObservationRepository) {
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
        this.weatherObservationRepository = weatherObservationRepository;
    }

    public BigDecimal getBaseFee(String city, VehicleType vehicleType) {
        RegionalBaseFee baseFee = regionalBaseFeeRepository.findByCityAndVehicleType(city, vehicleType);

        if (baseFee == null) {
            throw new IllegalArgumentException("No base fee found for the city of" + city
                    + " for vehicle type" + vehicleType);
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
}
