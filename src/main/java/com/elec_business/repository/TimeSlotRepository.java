package com.elec_business.repository;

import com.elec_business.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Page<TimeSlot> findByStationId(String stationId, Pageable pageable);

    void deleteByStartTimeBefore(LocalDateTime now);

    @Query(value = """
        SELECT *
        FROM time_slot t
        WHERE t.station_id = :stationId
          AND t.availability && tsrange(
              CAST(:startTime AS timestamp),
              CAST(:endTime AS timestamp),
              :bounds
          )
        """, nativeQuery = true)
    List<TimeSlot> findSlotsInRange(
            @Param("stationId") String stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("bounds") String bounds
    );

    default boolean existsSlotInRange(String stationId, LocalDateTime start, LocalDateTime end, String bounds) {
        return !findSlotsInRange(stationId, start, end, bounds).isEmpty();
    }

    default Page<TimeSlot> findAvailableSlotsPage(String stationId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        List<TimeSlot> result = findSlotsInRange(stationId, start, end, "[]");
        return new PageImpl<>(result, pageable, result.size());
    }

    default Optional<TimeSlot> findSingleAvailableSlot(String stationId, LocalDateTime start, LocalDateTime end, String bounds) {
        return findSlotsInRange(stationId, start, end, bounds).stream().findFirst();
    }
}
