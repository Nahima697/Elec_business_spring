package com.elec_business.repository;

import com.elec_business.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
    List<TimeSlot> findByStationId(String stationId);
    @Query("""
    SELECT t FROM TimeSlot t
    WHERE t.station.id = :stationId
    AND t.startTime <= :startTime
    AND t.endTime >= :endTime
""")
    Page<TimeSlot> findAvailableTimeSlotsByPeriod(String stationId, LocalDateTime startTime, LocalDateTime endTime,Pageable pageable);

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
    boolean isSlotAvailable(String stationId, LocalDateTime start, LocalDateTime end);
    @Query(value = """
        SELECT *
        FROM time_slot
        WHERE station_id = :stationId
          AND availability && tsrange(
              CAST(:start AS timestamp),
              CAST(:end AS timestamp),
              '[)'
          )
    
""", nativeQuery = true)
    TimeSlot findSlotAvailableByStationIdBetweenStartDateTimeAndEndDateTime(String stationId, LocalDateTime start, LocalDateTime end);
    @Query(value = "SELECT (count(t) > 0) FROM time_slot t WHERE t.station_id = ?1 AND t.availability && tsrange(?2, ?3, '[)')", nativeQuery = true)
    boolean existsByStationAndAvailability(String stationId, LocalDateTime startTime, LocalDateTime endTime);

    void deleteByStartTimeBefore(LocalDateTime now);

    Page<TimeSlot> findByStationId(String stationId, Pageable pageable);

}
