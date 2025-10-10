package com.elec_business.data;

import com.elec_business.entity.*;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public List<ChargingStation> stations = new ArrayList<>();
    public List<User> users = new ArrayList<>();
    public List<Booking> bookings = new ArrayList<>();

    public record LoadResult(List<ChargingStation> stations, List<User> users, List<Booking> bookings) {}
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public LoadResult load() {

        UserRole roleUser;
        try {
            roleUser = em.createQuery("SELECT r FROM UserRole r WHERE r.name = :name", UserRole.class)
                    .setParameter("name", "ROLE_USER")
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            roleUser = new UserRole(null, "ROLE_USER");
            em.persist(roleUser);
            em.flush();
        }

        em.persist(roleUser);
        em.flush();

        // USER 1
        User user1;
        try {
            user1 = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", "user1@test.com")
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            user1 = new User();
            user1.setUsername("user1");
            user1.setEmail("user1@test.com");
            user1.setPassword(encoder.encode("password123"));
            user1.setPhoneNumber("0600000001");
            user1.setEmailVerified(true);
            user1.setPhoneVerified(true);
            user1.setRole(roleUser);
            user1.setCreatedAt(Instant.now());
            user1.setEmailVerifiedAt(Instant.now());
            user1.setPhoneVerifiedAt(Instant.now());
            em.persist(user1);
            em.flush();
        }

        // USER 2
        User user2;
        try {
            user2 = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", "user2@test.com")
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            user2 = new User();
            user2.setUsername("user2");
            user2.setEmail("user2@test.com");
            user2.setPassword(encoder.encode("password223"));
            user2.setPhoneNumber("0600000002");
            user2.setEmailVerified(true);
            user2.setPhoneVerified(true);
            user2.setRole(roleUser);
            user2.setCreatedAt(Instant.now());
            user2.setEmailVerifiedAt(Instant.now());
            user2.setPhoneVerifiedAt(Instant.now());
            em.persist(user2);
            em.flush();
        }

        // USER 3
        User user3;
        try {
            user3 = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", "user3@test.com")
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            user3 = new User();
            user3.setUsername("user3");
            user3.setEmail("user3@test.com");
            user3.setPassword(encoder.encode("password323"));
            user3.setPhoneNumber("0600000003");
            user3.setEmailVerified(true);
            user3.setPhoneVerified(true);
            user3.setRole(roleUser);
            user3.setCreatedAt(Instant.now());
            user3.setEmailVerifiedAt(Instant.now());
            user3.setPhoneVerifiedAt(Instant.now());
            em.persist(user3);
            em.flush();
        }

        // LOCATION
        ChargingLocation location1 = new ChargingLocation(
                null, "1 rue Lyon", "69007", "Lyon", "France", "Lyon7", user1, new HashSet<>()
        );
        em.persist(location1);

        // STATIONS
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
        em.flush(); // flush stations

        // TIMESLOT
        LocalDateTime now = LocalDateTime.now();
        TimeSlot slot = new TimeSlot();
        slot.setStation(station1);
        slot.setStartTime(now.plusHours(1));
        slot.setEndTime(now.plusHours(8));
        slot.setIsAvailable(true);
        slot.setAvailability(Range.closed(slot.getStartTime(), slot.getEndTime()));
        em.persist(slot);

        /// BOOKING STATUS
        BookingStatus pendingStatus = new BookingStatus(BookingStatusType.PENDING);
        BookingStatus acceptedStatus = new BookingStatus(BookingStatusType.ACCEPTED);
        BookingStatus rejectedStatus = new BookingStatus(BookingStatusType.REJECTED);
        BookingStatus cancelledStatus = new BookingStatus(BookingStatusType.CANCELLED);
        em.persist(pendingStatus);
        em.persist(acceptedStatus);
        em.persist(rejectedStatus);
        em.persist(cancelledStatus);

        // Booking dans le futur
        Booking booking1 = new Booking();
        booking1.setUser(user1);
        booking1.setStation(station1);
        booking1.setStartDate(now.plusHours(2));
        booking1.setEndDate(now.plusHours(4));
        booking1.setTotalPrice(new BigDecimal("0.50"));
        booking1.setCreatedAt(Instant.now());
        booking1.setStatus(pendingStatus);
        em.persist(booking1);
        em.flush();

        // Ajout des objets dans les listes
        users.clear();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        stations.clear();
        stations.add(station1);
        stations.add(station2);

        bookings.clear();
        bookings.add(booking1);

        return new LoadResult(stations, users, bookings);
    }

}
