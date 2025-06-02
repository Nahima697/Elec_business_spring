package com.elec_business.repository;

import com.elec_business.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChargingStationRepository  extends JpaRepository<ChargingStation, UUID> {
    Optional<ChargingStation> findById(UUID stationId);

}
