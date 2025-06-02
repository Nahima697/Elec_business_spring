package com.elec_business.repository;

import com.elec_business.entity.ChargingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChargingLocationRepository extends JpaRepository<ChargingLocation, Long> {

    ChargingLocation findChargingLocationBy(String name);

    ChargingLocation findChargingLocationById(UUID id);
}
