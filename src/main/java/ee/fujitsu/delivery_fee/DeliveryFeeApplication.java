package ee.fujitsu.delivery_fee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeliveryFeeApplication {

	static void main(String[] args) {
		SpringApplication.run(DeliveryFeeApplication.class, args);
	}

}
