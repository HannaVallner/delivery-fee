package ee.fujitsu.delivery_fee.controller;

import ee.fujitsu.delivery_fee.entity.VehicleType;
import ee.fujitsu.delivery_fee.service.DeliveryFeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping("/api/delivery-fee")
    public BigDecimal calculateDeliveryFee(
            @RequestParam String city,
            @RequestParam VehicleType vehicleType) {

        return deliveryFeeService.calculateDeliveryFee(city, vehicleType);
    }
}
