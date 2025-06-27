package com.elec_business.repository;

import com.elec_business.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    List<TimeSlot> findByStationId(UUID stationId);
    @Query("""
    SELECT t FROM TimeSlot t
    WHERE t.station.id = :stationId
    AND t.startTime <= :startTime
    AND t.endTime >= :endTime
""")
    List<TimeSlot> findAvailableTimeSlots(UUID stationId, Instant startTime, Instant endTime);

    @Query("""
 SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
 FROM TimeSlot t
 WHERE t.station.id = :stationId
 AND t.startTime <= :start
 AND t.endTime >= :end
""")
    boolean isSlotAvailable(UUID stationId, Instant start, Instant end);

}
