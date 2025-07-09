package com.elec_business.availability.repository;

import com.elec_business.availability.model.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<TimeSlot> findAvailableTimeSlotsByPeriod(UUID stationId, Instant startTime, Instant endTime,Pageable pageable);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM time_slot
        WHERE station_id = :stationId
          AND availability && tsrange(
              CAST(:start AS timestamp),
              CAST(:end AS timestamp),
              '[)'
          )
    )
""", nativeQuery = true)
    boolean isSlotAvailable(UUID stationId, Instant start, Instant end);

    @Query(value = "SELECT (count(t) > 0) FROM time_slot t WHERE t.station_id = ?1 AND t.availability && tsrange(?2, ?3, '[)')", nativeQuery = true)
    boolean existsByStationAndAvailability(UUID stationId, Instant startTime, Instant endTime);

    void deleteByStartTimeBefore(Instant now);

    Page<TimeSlot> findByStationId(UUID stationId, Pageable pageable);

}
