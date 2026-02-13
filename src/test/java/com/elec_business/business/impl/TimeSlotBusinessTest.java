package com.elec_business.business.impl;

import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.controller.mapper.TimeSlotMapper;
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

    @Mock
    private TimeSlotMapper timeSlotMapper;

    @InjectMocks
    private TimeSlotBusinessImpl timeSlotBusiness;

    @Test
    void addTimeSlot_Success() {
        String stationId = "station-1";
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);

        ChargingStation station = new ChargingStation();
        station.setId(stationId);

        TimeSlot savedSlot = new TimeSlot();
        savedSlot.setStartTime(start);
        savedSlot.setEndTime(end);

        TimeSlotResponseDto dto = mock(TimeSlotResponseDto.class);

        when(chargingStationRepository.findById(stationId))
                .thenReturn(Optional.of(station));

        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any()))
                .thenReturn(false);

        when(timeSlotRepository.save(any(TimeSlot.class)))
                .thenReturn(savedSlot);

        when(timeSlotMapper.toDto(savedSlot))
                .thenReturn(dto);

        TimeSlotResponseDto result =
                timeSlotBusiness.addTimeSlot(stationId, start, end);

        assertNotNull(result);
        verify(timeSlotRepository).save(any(TimeSlot.class));
        verify(timeSlotMapper).toDto(savedSlot);
    }

    @Test
    void addTimeSlot_StationNotFound() {
        String stationId = "unknown";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        when(chargingStationRepository.findById(stationId))
                .thenReturn(Optional.empty());

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

        when(timeSlotRepository.findSlotsInRange(
                eq(stationId), eq(start), eq(end), anyString()))
                .thenReturn(List.of(slot1));

        timeSlotBusiness.setTimeSlotAvailability(stationId, start, end);

        assertFalse(slot1.getIsAvailable());
        verify(timeSlotRepository).saveAll(anyList());
    }

    @Test
    void generateTimeSlotsFromAvailabilityRules_Success() {

        ChargingStation station = new ChargingStation();
        station.setId("s1");

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);
        rule.setDayOfWeek(1);
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(12, 0));

        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 2);

        when(timeSlotRepository.existsSlotInRange(any(), any(), any(), any()))
                .thenReturn(false);

        timeSlotBusiness.generateTimeSlotsFromAvailabilityRules(
                start, end, List.of(rule));

        verify(timeSlotRepository).saveAll(anyList());
    }

    @Test
    void purgeOldTimeSlots_Success() {
        timeSlotBusiness.purgeOldTimeSlots();
        verify(timeSlotRepository)
                .deleteByStartTimeBefore(any(LocalDateTime.class));
    }

    @Test
    void getAvailableSlots_Success() {
        Pageable pageable = Pageable.unpaged();

        TimeSlot slot = new TimeSlot();
        Page<TimeSlot> page = Page.empty();

        when(timeSlotRepository.findByStationId("s1", pageable))
                .thenReturn(page);

        when(timeSlotMapper.toDto(any()))
                .thenReturn(mock(TimeSlotResponseDto.class));

        Page<TimeSlotResponseDto> result =
                timeSlotBusiness.getAvailableSlots("s1", pageable);

        assertNotNull(result);
        verify(timeSlotRepository)
                .findByStationId("s1", pageable);
    }

    @Test
    void getAvailableSlotsByPeriod_Success() {
        Pageable pageable = Pageable.unpaged();

        Page<TimeSlot> page = Page.empty();

        when(timeSlotRepository.findAvailableSlotsPage(
                any(), any(), any(), any()))
                .thenReturn(page);

        Page<TimeSlotResponseDto> result =
                timeSlotBusiness.getAvailableSlotsByPeriod(
                        "s1",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1),
                        pageable
                );

        assertNotNull(result);
    }

    @Test
    void getSlotsFiltered_Success() {

        when(timeSlotRepository.findAll(any(Specification.class)))
                .thenReturn(Collections.emptyList());

        List<TimeSlotResponseDto> result =
                timeSlotBusiness.getSlotsFiltered("s1", LocalDate.now());

        assertNotNull(result);
        verify(timeSlotRepository)
                .findAll(any(Specification.class));
    }
}
