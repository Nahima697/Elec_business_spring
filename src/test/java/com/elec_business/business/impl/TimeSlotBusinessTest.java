package com.elec_business.business.impl;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.TimeSlot;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Test
    void addTimeSlot_Success() {
        String stationId = "station-1";
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        ChargingStation station = new ChargingStation();
        station.setId(stationId);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(false);

        timeSlotBusiness.addTimeSlot(stationId, start, end);

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

    @Test
    void setTimeSlotAvailability_Success() {
        String stationId = "s1";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        TimeSlot slot1 = new TimeSlot();
        slot1.setIsAvailable(true);

        when(timeSlotRepository.findSlotsInRange(eq(stationId), eq(start), eq(end), anyString()))
                .thenReturn(List.of(slot1));

        timeSlotBusiness.setTimeSlotAvailability(stationId, start, end);

        assertFalse(slot1.getIsAvailable());
        verify(timeSlotRepository).saveAll(anyList());
    }

    @Test
    void generateTimeSlotsFromAvailabilityRules_Success() {
        // ARRANGE
        ChargingStation station = new ChargingStation();
        station.setId("s1");

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);
        rule.setDayOfWeek(1); // Lundi
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(12, 0));

        // On prend une date qui EST un lundi (ex: 1er Janvier 2024)
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 2);

        // Mock : Pas de slot existant, donc on peut créer
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(false);

        // ACT
        timeSlotBusiness.generateTimeSlotsFromAvailabilityRules(start, end, List.of(rule));

        // ASSERT
        // Capture pour vérifier ce qui est sauvegardé
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TimeSlot>> captor = ArgumentCaptor.forClass(List.class);
        verify(timeSlotRepository).saveAll(captor.capture());

        List<TimeSlot> savedSlots = captor.getValue();
        assertEquals(1, savedSlots.size()); // 1 seul slot car 1 seul lundi dans l'intervalle
        assertEquals(LocalTime.of(8, 0), savedSlots.get(0).getStartTime().toLocalTime());
    }

    @Test
    void purgeOldTimeSlots_Success() {
        timeSlotBusiness.purgeOldTimeSlots();
        // Le mockito 'any' marche ici
        verify(timeSlotRepository).deleteByStartTimeBefore(any(LocalDateTime.class));
    }

    @Test
    void getAvailableSlots_Success() {
        Pageable pageable = Pageable.unpaged();
        when(timeSlotRepository.findByStationId("s1", pageable)).thenReturn(Page.empty());

        timeSlotBusiness.getAvailableSlots("s1", pageable);

        verify(timeSlotRepository).findByStationId("s1", pageable);
    }

    @Test
    void getSlotsFiltered_Success() {
        when(timeSlotRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        timeSlotBusiness.getSlotsFiltered("s1", LocalDate.now());

        verify(timeSlotRepository).findAll(any(Specification.class));
    }
}