package com.elec_business.repository;

import com.elec_business.entity.ChargingLocation;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChargingLocationRepository extends JpaRepository<ChargingLocation, String> {

    ChargingLocation findChargingLocationByName(String name);
    ChargingLocation findChargingLocationById(String id);

    List<ChargingLocation> findChargingLocationByUserId(String userId);

    List<ChargingLocation> findByUser(User user);

    @Query("SELECT DISTINCT cs FROM ChargingStation cs " +
            "LEFT JOIN FETCH cs.reviews " +
            "LEFT JOIN FETCH cs.location loc " +
            "LEFT JOIN FETCH loc.user " +
            "WHERE loc.id = :id")
    List<ChargingStation> findByLocation_Id(@Param("id") String id);
}
