package com.elec_business.repository;

import com.elec_business.entity.ChargingLocation;
import com.elec_business.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChargingLocationRepository extends JpaRepository<ChargingLocation, String> {

    ChargingLocation findChargingLocationByName(String name);
    ChargingLocation findChargingLocationById(String id);

    List<ChargingLocation> findChargingLocationByUserId(String userId);

    List<ChargingLocation> findByUser(User user);

}
