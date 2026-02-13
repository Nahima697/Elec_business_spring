package com.elec_business.data;

import com.elec_business.entity.*;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class TestDataLoader {

    @Autowired
    EntityManager em;

    public List<ChargingStation> stations = new ArrayList<>();
    public List<User> users = new ArrayList<>();
    public List<Booking> bookings = new ArrayList<>();

    public record LoadResult(List<ChargingStation> stations, List<User> users, List<Booking> bookings) {}

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Transactional
    public LoadResult load() {

        // ================================
        // 1) ROLES
        // ================================
        UserRole roleOwner = findOrCreateRole("OWNER");
        UserRole roleRenter = findOrCreateRole("RENTER");
        em.flush();

        // ================================
        // 2) USERS
        // ================================
        // user1 = OWNER (owns stations)
        User user1 = findOrCreateUser(
                "user1", "user1@test.com", "password123",
                roleOwner, roleRenter
        );

        // user2 = RENTER (creates bookings)
        User user2 = findOrCreateUser(
                "user2", "user2@test.com", "password223",
                roleRenter
        );

        User user3 = findOrCreateUser(
                "user3", "user3@test.com", "password323",
                roleOwner
        );

        // ================================
        // 3) LOCATION (propriétaire = user1)
        // ================================
        ChargingLocation location1 = new ChargingLocation(
                null,
                "1 rue Lyon",
                "69007",
                "Lyon",
                "France",
                "Lyon7",
                user1,
                new HashSet<>()
        );
        em.persist(location1);

        // ================================
        // 4) STATIONS (owned by user1)
        // ================================
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

        em.flush();

        // ================================
        // 5) TIMESLOT (pour station1)
        // ================================
        LocalDateTime now = LocalDateTime.now();
        TimeSlot slot = new TimeSlot();
        slot.setStation(station1);
        slot.setStartTime(now.plusHours(1));
        slot.setEndTime(now.plusHours(12));
        slot.setIsAvailable(true);
        slot.setAvailability(Range.closed(slot.getStartTime(), slot.getEndTime()));
        em.persist(slot);

        // ================================
        // 6) BOOKING STATUS - TOUS LES STATUTS
        // ================================
        BookingStatus pending = findOrCreateStatus(BookingStatusType.PENDING);
        BookingStatus accepted = findOrCreateStatus(BookingStatusType.ACCEPTED);
        BookingStatus rejected = findOrCreateStatus(BookingStatusType.REJECTED);
        em.flush();

        // ================================
        // 7) BOOKINGS - UN PAR TEST !
        // ================================

        // Booking 1: user2 books user1's station → pour ACCEPT test
        Booking booking1 = new Booking();
        booking1.setUser(user2);
        booking1.setStation(station1);
        booking1.setStartDate(now.plusHours(2));
        booking1.setEndDate(now.plusHours(3));
        booking1.setTotalPrice(new BigDecimal("0.25"));
        booking1.setCreatedAt(Instant.now());
        booking1.setStatus(pending);
        em.persist(booking1);

        // Booking 2: user2 books user1's station → pour REJECT test
        Booking booking2 = new Booking();
        booking2.setUser(user2);
        booking2.setStation(station1);
        booking2.setStartDate(now.plusHours(4));
        booking2.setEndDate(now.plusHours(5));
        booking2.setTotalPrice(new BigDecimal("0.25"));
        booking2.setCreatedAt(Instant.now());
        booking2.setStatus(pending);
        em.persist(booking2);

        // Booking 3: user1 books his own station → pour GET BY ID test
        Booking booking3 = new Booking();
        booking3.setUser(user1);
        booking3.setStation(station1);
        booking3.setStartDate(now.plusHours(6));
        booking3.setEndDate(now.plusHours(7));
        booking3.setTotalPrice(new BigDecimal("0.25"));
        booking3.setCreatedAt(Instant.now());
        booking3.setStatus(pending);
        em.persist(booking3);

        // Booking 4: user1 books his own station → pour UPDATE test
        Booking booking4 = new Booking();
        booking4.setUser(user1);
        booking4.setStation(station1);
        booking4.setStartDate(now.plusHours(8));
        booking4.setEndDate(now.plusHours(9));
        booking4.setTotalPrice(new BigDecimal("0.25"));
        booking4.setCreatedAt(Instant.now());
        booking4.setStatus(pending);
        em.persist(booking4);

        // Booking 5: user1 books his own station → pour DELETE test
        Booking booking5 = new Booking();
        booking5.setUser(user1);
        booking5.setStation(station1);
        booking5.setStartDate(now.plusHours(10));
        booking5.setEndDate(now.plusHours(11));
        booking5.setTotalPrice(new BigDecimal("0.25"));
        booking5.setCreatedAt(Instant.now());
        booking5.setStatus(pending);
        em.persist(booking5);

        em.flush();
        em.refresh(booking1);
        em.refresh(booking2);
        em.refresh(booking3);
        em.refresh(booking4);
        em.refresh(booking5);

        // ================================
        // 8) STOCKER EN MEMOIRE
        // ================================
        users = List.of(user1, user2, user3);
        stations = List.of(station1, station2);
        bookings = List.of(booking1, booking2, booking3, booking4, booking5);

        return new LoadResult(stations, users, bookings);
    }

    // ===========================================================
    // HELPERS
    // ===========================================================

    private UserRole findOrCreateRole(String name) {
        try {
            return em.createQuery("SELECT r FROM UserRole r WHERE r.name = :n", UserRole.class)
                    .setParameter("n", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            UserRole r = new UserRole(null, name);
            em.persist(r);
            return r;
        }
    }

    private BookingStatus findOrCreateStatus(BookingStatusType type) {
        try {
            return em.createQuery("SELECT b FROM BookingStatus b WHERE b.name = :n", BookingStatus.class)
                    .setParameter("n", type)
                    .getSingleResult();
        } catch (NoResultException e) {
            BookingStatus b = new BookingStatus(type);
            em.persist(b);
            return b;
        }
    }

    private User findOrCreateUser(String username, String email, String pwd, UserRole... roles) {
        try {

            return em.createQuery(
                            "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email",
                            User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            User u = new User();
            u.setUsername(username);
            u.setEmail(email);
            u.setPassword(encoder.encode(pwd));
            u.setPhoneNumber("0600000000");
            u.setEmailVerified(true);
            u.setPhoneVerified(true);
            u.setCreatedAt(Instant.now());
            u.setEmailVerifiedAt(Instant.now());
            u.setPhoneVerifiedAt(Instant.now());

            Set<UserRole> roleSet = new HashSet<>(Arrays.asList(roles));
            u.setRoles(roleSet);

            em.persist(u);
            em.flush();

            em.refresh(u);
            return u;
        }
    }
}