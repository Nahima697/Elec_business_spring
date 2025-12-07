package com.elec_business.repository;

import com.elec_business.entity.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, String> {
    List<AvailabilityRule> findByChargingStation_Id(String chargingStationId);
}
