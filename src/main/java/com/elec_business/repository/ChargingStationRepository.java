package com.elec_business.repository;

import com.elec_business.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChargingStationRepository  extends JpaRepository<ChargingStation, UUID> {
    ChargingStation findChargingStationByName(String name);
}
