package com.elec_business.repository;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;


@Repository
public interface ChargingStationRepository  extends JpaRepository<ChargingStation, String> {
    ChargingStation findChargingStationByName(String name);
    List<ChargingStation>  findByLocation_Id(String locationId);
    ChargingStation findByAvailabilityRulesContaining(AvailabilityRule availabilityRule);
    Page<ChargingStation> findAll(Pageable pageable);
}
