package com.elec_business;

import com.elec_business.entity.*;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class TestDataLoader {

    @Autowired
    EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<ChargingStation> stations = new ArrayList<>();
    public List<User> users = new ArrayList<>();
    public List<Booking> bookings = new ArrayList<>();

    public record LoadResult(List<ChargingStation> stations, List<User> users, List<Booking> bookings) {}

    @Transactional
    public LoadResult load() {

        // ROLE
        UserRole roleUser = new UserRole(null, "ROLE_USER");
        em.persist(roleUser);

        // USERS
        User user1 = new User(
                null, "user1", "user1@test.com", passwordEncoder.encode("password123"),
                "0600000001", true, true, null, roleUser,
                Instant.now(), Instant.now(), Instant.now(),
                null, null, null
        );
        em.persist(user1);

        User user2 = new User(
                null, "user2", "user2@test.com", passwordEncoder.encode("password456"),
                "0600000002", true, true, null, roleUser,
                Instant.now(), Instant.now(), Instant.now(),
                null, null, null
        );
        em.persist(user2);

        User user3 = new User(
                null, "user3", "user3@test.com", passwordEncoder.encode("password789"),
                "0600000003", true, true, null, roleUser,
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
        station1.setPowerKw(new BigDecimal("50.00"));
        station1.setPrice(new BigDecimal("0.25"));
        station1.setCreatedAt(Instant.now());
        station1.setLat(new BigDecimal("45.750000"));
        station1.setLng(new BigDecimal("4.850000"));
        station1.setLocation(location1);
        station1.setImageUrl(null);
        em.persist(station1);

        // STATION 2
        ChargingStation station2 = new ChargingStation();
        station2.setName("Station B");
        station2.setDescription("Borne classique 22kW");
        station2.setPowerKw(new BigDecimal("22.00"));
        station2.setPrice(new BigDecimal("0.18"));
        station2.setCreatedAt(Instant.now());
        station2.setLat(new BigDecimal("45.751000"));
        station2.setLng(new BigDecimal("4.851000"));
        station2.setLocation(location1);
        station2.setImageUrl(null);
        em.persist(station2);

        // TIMESLOT
        LocalDateTime start = LocalDateTime.of(2025, 9, 10, 8, 20);
        LocalDateTime end = LocalDateTime.of(2025, 9, 10, 18, 20);

        TimeSlot slot = new TimeSlot();
        slot.setStation(station1);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setIsAvailable(true);

        slot.setAvailability(Range.closed(start, end));

        em.persist(slot);

        Booking booking1 = new Booking();
        booking1.setUser(user1);
        booking1.setStation(station1);
        booking1.setStartDate(LocalDateTime.of(2025, 9, 10, 11, 20));
        booking1.setEndDate(LocalDateTime.of(2025, 9, 10, 13, 20));
        booking1.setTotalPrice(new BigDecimal("0.50"));
        booking1.setCreatedAt(Instant.now());

        // BOOKING STATUS

        BookingStatus pendingStatus = new BookingStatus(BookingStatusType.PENDING);
        em.persist(pendingStatus);

        BookingStatus acceptedStatus = new BookingStatus(BookingStatusType.ACCEPTED);
        em.persist(acceptedStatus);


        BookingStatus rejectedStatus = new BookingStatus(BookingStatusType.REJECTED);
        em.persist(rejectedStatus);

        BookingStatus cancelledStatus = new BookingStatus(BookingStatusType.CANCELLED);
        em.persist(cancelledStatus);
        booking1.setStatus(pendingStatus);

        em.persist(booking1);
        em.flush();

        // Ajout des Objets
        users.clear();
        stations.clear();
        bookings.clear();

        users.add(user1);
        users.add(user2);
        users.add(user3);

        stations.add(station1);
        stations.add(station2);
        bookings.add(booking1);

        return new LoadResult(stations, users, bookings);
    }
}
