package com.elec_business.repository;

import com.elec_business.entity.Booking;
import com.elec_business.entity.BookingStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    @Query("""
        SELECT b
        FROM Booking b
        LEFT JOIN FETCH b.station s
        LEFT JOIN FETCH s.location l
        LEFT JOIN FETCH l.user owner
        LEFT JOIN FETCH b.user renter
        LEFT JOIN FETCH b.status status
        WHERE b.id = :id
    """)
    Optional<Booking> findByIdWithDetails(@Param("id") String id);

    Booking findBookingById(String id);
    @Query("""
    SELECT b
    FROM Booking b
    JOIN FETCH b.user u
    JOIN FETCH b.status st
    JOIN FETCH b.station s
    JOIN FETCH s.location l
    LEFT JOIN FETCH l.user owner
    WHERE u.id = :userId
    ORDER BY b.createdAt DESC
""")
    List<Booking> findByUserId(@Param("userId") String userId);


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
    SELECT b FROM Booking b
    JOIN FETCH b.user u
    JOIN FETCH b.status
    JOIN FETCH b.station s
    JOIN FETCH s.location l
    JOIN FETCH l.user owner
    WHERE owner.id = :ownerId
    ORDER BY b.createdAt DESC
""")
    List<Booking> findByStationOwner(@Param("ownerId") String ownerId);

    boolean existsByStation_Location_User_IdAndStation_IdAndStatus_Name(
            String userId,
            String stationId,
            BookingStatusType status
    );
    boolean existsByUser_IdAndStation_IdAndStatus_Name(
            String userId,
            String stationId,
            BookingStatusType status
    );
}



