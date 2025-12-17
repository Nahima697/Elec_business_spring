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
import com.elec_business.config.TestcontainersConfiguration;

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
        // 1. On charge toutes les données via le Loader (Users, Stations, Locations...)
        TestDataLoader.LoadResult result = testDataLoader.load();
        
        // 2. On récupère la station A créée par le loader
        this.station = result.stations().get(0); 

        // 3. On ajoute les TimeSlots spécifiques à ce test (le loader ne crée qu'un slot de base)
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
