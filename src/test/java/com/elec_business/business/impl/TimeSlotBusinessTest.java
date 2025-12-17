package com.elec_business.business.impl;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.TimeSlot;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSlotBusinessTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @InjectMocks
    private TimeSlotBusinessImpl timeSlotBusiness;

    // --- TEST: addTimeSlot ---
    @Test
    void addTimeSlot_Success() {
        // ARRANGE
        String stationId = "station-1";
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        
        ChargingStation station = new ChargingStation();
        station.setId(stationId);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        timeSlotBusiness.addTimeSlot(stationId, start, end);

        // ASSERT
        verify(timeSlotRepository).save(any(TimeSlot.class));
    }

    @Test
    void addTimeSlot_StationNotFound() {
        String stationId = "unknown";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            timeSlotBusiness.addTimeSlot(stationId, start, end)
        );
    }

    // --- TEST: setTimeSlotAvailability ---
    @Test
    void setTimeSlotAvailability_Success() {
        // ARRANGE
        String stationId = "s1";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        TimeSlot slot1 = new TimeSlot(); 
        slot1.setIsAvailable(true); // Disponible avant

        when(timeSlotRepository.findSlotsInRange(eq(stationId), eq(start), eq(end), anyString()))
                .thenReturn(List.of(slot1));

        // ACT
        timeSlotBusiness.setTimeSlotAvailability(stationId, start, end);

        // ASSERT
        assertFalse(slot1.getIsAvailable()); // Doit devenir indisponible
        verify(timeSlotRepository).saveAll(anyList());
    }

    // --- TEST: generateTimeSlotsFromAvailabilityRules ---
    @Test
    void generateTimeSlotsFromAvailabilityRules_Success() {
        // ARRANGE
        ChargingStation station = new ChargingStation();
        station.setId("s1");

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station); // Correction nom méthode
        rule.setDayOfWeek(1); // 1 = Lundi
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(12, 0));

        // On génère sur une période qui contient un Lundi
        LocalDate start = LocalDate.of(2024, 1, 1); // C'est un Lundi
        LocalDate end = LocalDate.of(2024, 1, 2);   // Mardi

        // ACT
        timeSlotBusiness.generateTimeSlotsFromAvailabilityRules(start, end, List.of(rule));

        // ASSERT
        // On vérifie qu'on sauvegarde bien une liste de slots générés
        verify(timeSlotRepository).saveAll(argThat(list -> {
            List<TimeSlot> slots = (List<TimeSlot>) list;
            return slots.size() == 1 && // Un seul slot généré (le lundi)
                   slots.get(0).getStartTime().toLocalTime().equals(LocalTime.of(8,0));
        }));
    }

    // --- TEST: purgeOldTimeSlots ---
    @Test
    void purgeOldTimeSlots_Success() {
        timeSlotBusiness.purgeOldTimeSlots();
        verify(timeSlotRepository).deleteByStartTimeBefore(any(LocalDateTime.class));
    }

    // --- TEST: getAvailableSlots ---
    @Test
    void getAvailableSlots_Success() {
        Pageable pageable = Pageable.unpaged();
        when(timeSlotRepository.findByStationId("s1", pageable)).thenReturn(Page.empty());
        
        timeSlotBusiness.getAvailableSlots("s1", pageable);
        
        verify(timeSlotRepository).findByStationId("s1", pageable);
    }

    // --- TEST: getSlotsFiltered ---
    @Test
    void getSlotsFiltered_Success() {
        
        when(timeSlotRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        timeSlotBusiness.getSlotsFiltered("s1", LocalDate.now());

        verify(timeSlotRepository).findAll(any(Specification.class));
    }
}
