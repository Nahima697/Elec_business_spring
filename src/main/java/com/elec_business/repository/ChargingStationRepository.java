package com.elec_business.repository;

import com.elec_business.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChargingStationRepository  extends JpaRepository<ChargingStation, UUID> {
    ChargingStation findStationById(UUID stationId);

    ChargingStation findChargingStationByName(String name);
}
