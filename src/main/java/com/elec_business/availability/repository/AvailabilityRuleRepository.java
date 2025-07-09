package com.elec_business.availability.repository;

import com.elec_business.availability.model.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, UUID> {
    List<AvailabilityRule> findByChargingStation_Id(UUID chargingStationId);
}
