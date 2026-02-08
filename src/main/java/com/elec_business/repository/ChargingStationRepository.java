package com.elec_business.repository;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;


@Repository
public interface ChargingStationRepository  extends JpaRepository<ChargingStation, String> {
    ChargingStation findChargingStationByName(String name);
    @Query("SELECT DISTINCT cs FROM ChargingStation cs " +
            "LEFT JOIN FETCH cs.reviews " +
            "LEFT JOIN FETCH cs.location loc " +
            "LEFT JOIN FETCH loc.user " +
            "WHERE loc.id = :id")
    List<ChargingStation> findByLocation_Id(@Param("id") String id);
    @Query("""
        SELECT s
        FROM ChargingStation s
        LEFT JOIN FETCH s.location l
        LEFT JOIN FETCH l.user u
        LEFT JOIN FETCH s.images
        LEFT JOIN FETCH s.reviews r
        LEFT JOIN FETCH r.user ru
        WHERE s.id = :id
    """)
    Optional<ChargingStation> findByIdWithDetails(@Param("id") String id);
    @Query("SELECT s FROM ChargingStation s WHERE s.location.user.email = :email")
    List<ChargingStation> findByOwnerEmail(String email);

    Page<ChargingStation> findAll(Pageable pageable);
}
