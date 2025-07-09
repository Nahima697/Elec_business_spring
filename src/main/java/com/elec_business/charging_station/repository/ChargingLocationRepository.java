package com.elec_business.charging_station.repository;

import com.elec_business.charging_station.model.ChargingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChargingLocationRepository extends JpaRepository<ChargingLocation, UUID> {

    ChargingLocation findChargingLocationByName(String name);
    ChargingLocation findChargingLocationById(UUID id);
}
