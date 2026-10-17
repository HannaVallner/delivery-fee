package ee.fujitsu.delivery_fee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import ee.fujitsu.delivery_fee.service.WeatherImportService;

@SpringBootApplication
public class DeliveryFeeApplication {

	static void main(String[] args) {
		SpringApplication.run(DeliveryFeeApplication.class, args);
	}

	@Bean
	CommandLineRunner testWeatherDownload(WeatherImportService weatherImportService) {
		return args -> {
			weatherImportService.findRequiredStations();
		};
	}
}
