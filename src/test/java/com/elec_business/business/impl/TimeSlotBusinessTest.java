package com.elec_business.business.impl;

import com.elec_business.controller.dto.TimeSlotRequestDto;
import com.elec_business.entity.*;
import com.elec_business.repository.AvailabilityRuleRepository;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeSlotBusinessTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private AvailabilityRuleRepository availabilityRuleRepository;

    @InjectMocks
    private TimeSlotBusinessImpl timeSlotBusiness;

    // --- CREATE TIMESLOT ---

    @Test
    void createTimeSlot_Success() throws AccessDeniedException {
        // ARRANGE
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setId("s1");
        station.setLocation(loc);

        TimeSlotRequestDto dto = new TimeSlotRequestDto();
        dto.setStationId("s1");
        dto.setStartTime(LocalDateTime.now().plusHours(1));
        dto.setEndTime(LocalDateTime.now().plusHours(2));
        dto.setIsAvailable(true);

        when(chargingStationRepository.findById("s1")).thenReturn(Optional.of(station));
        // Pas de chevauchement
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(false);
        when(timeSlotRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        TimeSlot result = timeSlotBusiness.createTimeSlot(dto, owner);

        // ASSERT
        assertNotNull(result);
        assertEquals(dto.getStartTime(), result.getStartTime());
        verify(timeSlotRepository).save(any());
    }

    @Test
    void createTimeSlot_Fail_NotOwner() {
        User owner = new User(); owner.setId("owner-1");
        User hacker = new User(); hacker.setId("hacker-1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        when(chargingStationRepository.findById("s1")).thenReturn(Optional.of(station));

        TimeSlotRequestDto dto = new TimeSlotRequestDto();
        dto.setStationId("s1");

        assertThrows(AccessDeniedException.class, () ->
            timeSlotBusiness.createTimeSlot(dto, hacker)
        );
    }

    @Test
    void createTimeSlot_Fail_Overlap() {
        User owner = new User(); owner.setId("owner-1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);

        TimeSlotRequestDto dto = new TimeSlotRequestDto();
        dto.setStationId("s1");
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(1));

        when(chargingStationRepository.findById("s1")).thenReturn(Optional.of(station));
        // Simulation d'un conflit
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            timeSlotBusiness.createTimeSlot(dto, owner)
        );
        assertTrue(ex.getMessage().contains("overlaps"));
    }

    // --- UPDATE TIMESLOT ---

    @Test
    void updateTimeSlot_Success() throws AccessDeniedException {
        User owner = new User(); owner.setId("u1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);
        station.setId("s1");

        TimeSlot existing = new TimeSlot();
        existing.setId("ts1");
        existing.setStation(station);

        TimeSlotRequestDto dto = new TimeSlotRequestDto();
        dto.setStartTime(LocalDateTime.now().plusHours(5));
        dto.setEndTime(LocalDateTime.now().plusHours(6));
        dto.setIsAvailable(false);

        when(timeSlotRepository.findById("ts1")).thenReturn(Optional.of(existing));
        when(timeSlotRepository.existsSlotInRange(eq("s1"), any(), any(), eq("ts1"))).thenReturn(false);
        when(timeSlotRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        TimeSlot updated = timeSlotBusiness.updateTimeSlot("ts1", dto, owner);

        assertFalse(updated.getIsAvailable());
        assertEquals(dto.getStartTime(), updated.getStartTime());
    }

    // --- GENERATE SLOTS (Logique complexe) ---

    @Test
    void generateTimeSlotsForStation_Success() throws AccessDeniedException {
        // Ce test couvre la boucle de génération automatique
        User owner = new User(); owner.setId("u1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation();
        station.setId("s1");
        station.setLocation(loc);

        // Règle : Tous les LUNDIS de 8h à 12h
        AvailabilityRule rule = new AvailabilityRule();
        rule.setStation(station);
        rule.setDayOfWeek(DayOfWeek.MONDAY);
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(12, 0));
        rule.setIsRecurring(true);
        // Date de validité large
        rule.setStartDate(LocalDateTime.now().minusDays(1));
        rule.setEndDate(LocalDateTime.now().plusDays(10));

        when(chargingStationRepository.findById("s1")).thenReturn(Optional.of(station));
        when(availabilityRuleRepository.findByStationId("s1")).thenReturn(List.of(rule));
        // On dit qu'il n'y a pas de conflit pour laisser la boucle créer les slots
        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any())).thenReturn(false);

        // ACT
        timeSlotBusiness.generateTimeSlotsForStation("s1", owner);

        // ASSERT
        // On vérifie que save() a été appelé plusieurs fois (au moins 1 fois pour le lundi à venir)
        verify(timeSlotRepository, atLeastOnce()).save(any(TimeSlot.class));
    }

    // --- SET AVAILABILITY (Réservation) ---

    @Test
    void setTimeSlotAvailability_Success() {
        String stationId = "s1";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        // On simule qu'on trouve des slots concernés par la réservation
        TimeSlot slot1 = new TimeSlot(); slot1.setIsAvailable(true);
        TimeSlot slot2 = new TimeSlot(); slot2.setIsAvailable(true);
        
        when(timeSlotRepository.findSlotsInRange(eq(stationId), eq(start), eq(end), eq("[]")))
                .thenReturn(List.of(slot1, slot2));

        // ACT
        timeSlotBusiness.setTimeSlotAvailability(stationId, start, end);

        // ASSERT
        // Ils doivent être passés à indisponible (false)
        assertFalse(slot1.getIsAvailable());
        assertFalse(slot2.getIsAvailable());
        verify(timeSlotRepository).saveAll(anyList());
    }

    // --- DELETE ---

    @Test
    void deleteTimeSlot_Success() throws AccessDeniedException {
        User owner = new User(); owner.setId("u1");
        ChargingLocation loc = new ChargingLocation(); loc.setUser(owner);
        ChargingStation station = new ChargingStation(); station.setLocation(loc);
        
        TimeSlot slot = new TimeSlot();
        slot.setStation(station);

        when(timeSlotRepository.findById("ts1")).thenReturn(Optional.of(slot));

        timeSlotBusiness.deleteTimeSlot("ts1", owner);

        verify(timeSlotRepository).delete(slot);
    }
}
