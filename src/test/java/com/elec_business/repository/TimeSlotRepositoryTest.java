package com.elec_business.repository;

import com.elec_business.config.TestcontainersConfiguration;
import com.elec_business.data.TestDataLoader;
import com.elec_business.entity.*;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import({TestcontainersConfiguration.class, TestDataLoader.class})
@DataJpaTest
@ActiveProfiles("test")
class TimeSlotRepositoryTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TestDataLoader testDataLoader;

    private ChargingStation station;
    private TimeSlot slot1;
    private TimeSlot slot2;

    @BeforeEach
    void setUp() {
        // 1. Charge les données globales (Users, Stations, et le slot par défaut)
        TestDataLoader.LoadResult result = testDataLoader.load();
        this.station = result.stations().getFirst();

        // 2. NETTOYAGE CRITIQUE : On supprime le slot créé par le Loader
        timeSlotRepository.deleteAll(); 

        // 3. Création de nos données de test contrôlées
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
        Page<TimeSlot> page = timeSlotRepository.findByStationId(station.getId(), PageRequest.of(0, 10)); // J'ai remis size 10 pour simplifier
        
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
