package com.elec_business.repository;

import com.elec_business.entity.*;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("test")
class TimeSlotRepositoryTest  {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private ChargingLocationRepository chargingLocationRepository;

    private ChargingStation station;
    private TimeSlot slot1;
    private TimeSlot slot2;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Création rôle et utilisateur
        UserRole roleUser = userRoleRepository.save(new UserRole(null, "ROLE_USER"));
        User user1 = new User();
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
        userRepository.save(user1);
        // Création location
        ChargingLocation location1 = chargingLocationRepository.save(new ChargingLocation(
                null, "1 rue Lyon", "69007", "Lyon", "France", "Lyon7", user1, new HashSet<>()
        ));

        // Création stations
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
        chargingStationRepository.save(station1);

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
        chargingStationRepository.save(station2);

        this.station = station1;

        // Création de deux TimeSlots pour tests
        LocalDateTime now = LocalDateTime.now();
        slot1 = new TimeSlot();
        slot1.setStation(station);
        slot1.setStartTime(now.plusHours(1));
        slot1.setEndTime(now.plusHours(2));
        slot1.setIsAvailable(true);
        slot1.setAvailability(Range.closed(slot1.getStartTime(), slot1.getEndTime()));
        timeSlotRepository.save(slot1);

        slot2 = new TimeSlot();
        slot2.setStation(station);
        slot2.setStartTime(now.plusHours(3));
        slot2.setEndTime(now.plusHours(4));
        slot2.setIsAvailable(true);
        slot2.setAvailability(Range.closed(slot2.getStartTime(), slot2.getEndTime()));
        timeSlotRepository.save(slot2);
    }

    @Test
    void testFindByStationId() {
        List<TimeSlot> slots = timeSlotRepository.findByStationId(station.getId());
        assertThat(slots).hasSize(2);
    }

    @Test
    void testFindAvailableTimeSlotsByPeriod() {
        LocalDateTime start = slot1.getStartTime();
        LocalDateTime end = slot2.getEndTime();

        Page<TimeSlot> page = timeSlotRepository.findAvailableSlotsPage(
                station.getId(), start, end, PageRequest.of(0, 10)
        );

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testIsSlotAvailable() {
        boolean available = timeSlotRepository.existsSlotInRange(station.getId(), slot1.getStartTime(), slot1.getEndTime(),"[]");
        assertThat(available).isTrue();
    }

    @Test
    void testFindSlotAvailableByStationIdBetweenStartDateTimeAndEndDateTime() {
        List<TimeSlot> found = timeSlotRepository.findSlotsInRange(
                station.getId(), slot1.getStartTime(), slot1.getEndTime(),"[]"
        );
        assertThat(found).isNotNull();
    }

    @Test
    void testExistsByStationAndAvailability() {
        boolean exists = timeSlotRepository.existsSlotInRange(station.getId(), slot1.getStartTime(), slot1.getEndTime(),"[]");
        assertThat(exists).isTrue();
    }

    @Test
    void testDeleteByStartTimeBefore() {
        timeSlotRepository.deleteByStartTimeBefore(LocalDateTime.now().plusHours(5));
        assertThat(timeSlotRepository.findAll()).isEmpty();
    }

    @Test
    void testFindByStationIdWithPaging() {
        Page<TimeSlot> page = timeSlotRepository.findByStationId(station.getId(), PageRequest.of(0, 1));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
