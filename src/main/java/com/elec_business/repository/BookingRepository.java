package com.elec_business.repository;

import com.elec_business.entity.Booking;
import com.elec_business.entity.BookingStatusType;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String>, JpaSpecificationExecutor<Booking> {

    Booking findBookingById(String id);

    void deleteBookingById(String id);

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
            @Param("stationId") String stationId,
            @Param("bookingId") String bookingId);

    @Query("""
    SELECT b FROM Booking b\s
    WHERE b.station.location.user.id = :ownerId
    ORDER BY b.createdAt DESC
""")
    List<Booking> findByStationOwner(@Param("ownerId") String ownerId);

}



