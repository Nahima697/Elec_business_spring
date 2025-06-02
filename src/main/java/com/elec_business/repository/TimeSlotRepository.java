package com.elec_business.repository;

import com.elec_business.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    List<TimeSlot> findByStationId(UUID stationId);
    List<TimeSlot> findAvailableTimeSlots(UUID stationId, Instant startTime, Instant endTime);
    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM time_slot
        WHERE station_id = :stationId
        AND availability @> tsrange(:start, :end)
    )
""", nativeQuery = true)
    boolean isSlotAvailable(UUID stationId, Instant start, Instant end);

}
