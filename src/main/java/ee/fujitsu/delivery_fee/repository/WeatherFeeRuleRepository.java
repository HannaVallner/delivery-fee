package ee.fujitsu.delivery_fee.repository;

import ee.fujitsu.delivery_fee.entity.WeatherFeeRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherFeeRuleRepository extends JpaRepository<WeatherFeeRule, Long> {
}
