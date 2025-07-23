package com.elec_business.charging_station.repository;

import com.elec_business.charging_station.model.ChargingLocation;
import com.elec_business.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChargingLocationRepository extends JpaRepository<ChargingLocation, UUID> {

    ChargingLocation findChargingLocationByName(String name);
    ChargingLocation findChargingLocationById(UUID id);

    List<ChargingLocation> findChargingLocationByUserId(UUID userId);

    List<ChargingLocation> findByUser(AppUser user);
}
