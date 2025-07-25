package com.elec_business.repository;

import com.elec_business.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Booking findBookingById(UUID id);

    void deleteBookingById(UUID id);

    @Query("""
    SELECT b FROM Booking b
    WHERE b.station.id = :stationId
      AND b.id <> :bookingId
      AND b.status.name != 'CANCELLED' 
      AND b.startDate < :endDate
      AND b.endDate > :startDate
""")
    List<Booking> findConflictsExcludingBooking(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("stationId") UUID stationId,
            @Param("bookingId") UUID bookingId);
}



