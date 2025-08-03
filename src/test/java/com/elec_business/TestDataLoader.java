package com.elec_business;

import com.elec_business.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Component
public class TestDataLoader {

    @Autowired
    EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<String> stationIds;
    public List<String> userIds;
    public List<String> bookingIds;

    public record LoadResult(List<String> stationsIds, List<String> userIds, List<String> bookingsIds) {}

    @Transactional
    public LoadResult load() {

        // ROLE
        UserRole roleUser = new UserRole(null, "ROLE_USER");
        em.persist(roleUser);

        // USERS
        User user1 = new User(
                null, "user1", "user1@test.com", passwordEncoder.encode("password123"),
                "0600000001", null, true, null, roleUser,
                Instant.now(), Instant.now(), Instant.now(),
                null, null, null
        );
        em.persist(user1);

        User user2 = new User(
                null, "user2", "user2@test.com", passwordEncoder.encode("password456"),
                "0600000002", null, true, null, roleUser,
                Instant.now(), Instant.now(), Instant.now(),
                null, null, null
        );
        em.persist(user2);

        User user3 = new User(
                null, "user3", "user3@test.com", passwordEncoder.encode("password789"),
                "0600000003", null, true, null, roleUser,
                Instant.now(), Instant.now(), Instant.now(),
                null, null, null
        );
        em.persist(user3);

        // LOCATION
        ChargingLocation location1 = new ChargingLocation(
                null, "1 rue Lyon", "69007", "Lyon", "France", "Lyon7", user1, new HashSet<>()
        );
        em.persist(location1);

        // STATION 1
        ChargingStation station1 = new ChargingStation();
        station1.setName("Station A");
        station1.setDescription("Charge rapide 50kW");
        station1.setPowerKw(new java.math.BigDecimal("50.00"));
        station1.setPrice(new java.math.BigDecimal("0.25"));
        station1.setCreatedAt(Instant.now());
        station1.setLat(new java.math.BigDecimal("45.750000"));
        station1.setLng(new java.math.BigDecimal("4.850000"));
        station1.setLocation(location1);
        station1.setImageUrl(null);
        em.persist(station1);

        // STATION 2
        ChargingStation station2 = new ChargingStation();
        station2.setName("Station B");
        station2.setDescription("Borne classique 22kW");
        station2.setPowerKw(new java.math.BigDecimal("22.00"));
        station2.setPrice(new java.math.BigDecimal("0.18"));
        station2.setCreatedAt(Instant.now());
        station2.setLat(new java.math.BigDecimal("45.751000"));
        station2.setLng(new java.math.BigDecimal("4.851000"));
        station2.setLocation(location1);
        station2.setImageUrl(null);
        em.persist(station2);

        //Booking Status

        // BOOKING STATUS
        BookingStatus statusPending = new BookingStatus(null, "PENDING");
        BookingStatus statusConfirmed = new BookingStatus(null, "CONFIRMED");
        BookingStatus statusCancelled = new BookingStatus(null, "CANCELLED");
        em.persist(statusPending);
        em.persist(statusConfirmed);
        em.persist(statusCancelled);

        //BOOKING

        // BOOKINGS
        Booking booking1 = new Booking();
        booking1.setUser(user2);
        booking1.setStation(station1);
        booking1.setStartDate(Instant.now().plusSeconds(3600)); // dans 1h
        booking1.setEndDate(Instant.now().plusSeconds(7200));   // dans 2h
        booking1.setTotalPrice(new BigDecimal("0.50"));
        booking1.setStatus(statusPending);
        booking1.setCreatedAt(Instant.now());
        em.persist(booking1);

        Booking booking2 = new Booking();
        booking2.setUser(user3);
        booking2.setStation(station2);
        booking2.setStartDate(Instant.now().plusSeconds(86400)); // demain
        booking2.setEndDate(Instant.now().plusSeconds(90000));   // +1h
        booking2.setTotalPrice(new BigDecimal("0.18"));
        booking2.setStatus(statusConfirmed);
        booking2.setCreatedAt(Instant.now());
        em.persist(booking2);

        em.flush();
        bookingIds.add(booking1.getId());
        bookingIds.add(booking2.getId());
        userIds.add(user1.getId());
        userIds.add(user2.getId());
        userIds.add(user3.getId());
        stationIds.add(station1.getId());
        stationIds.add(station2.getId());
        return new LoadResult(
               userIds,stationIds,bookingIds
        );
    }
}
