package com.elec_business.repository;

import com.elec_business.entity.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.List;

public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, String> {

    List<AvailabilityRule> findByChargingStation_Id(String chargingStationId);

    @Query("""
        SELECT COUNT(r) > 0 FROM AvailabilityRule r
        WHERE r.chargingStation.id = :stationId
        AND r.dayOfWeek = :dayOfWeek
        AND (
            (r.startTime < :endTime AND r.endTime > :startTime)
        )
    """)
    boolean existsOverlappingRule(String stationId, Integer dayOfWeek, LocalTime startTime, LocalTime endTime);
}