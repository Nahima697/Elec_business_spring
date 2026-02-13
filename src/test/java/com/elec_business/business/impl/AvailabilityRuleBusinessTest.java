package com.elec_business.business.impl;

import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.business.exception.StationNotFoundException;
import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.controller.mapper.AvailabilityRuleMapper;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingLocation;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.elec_business.repository.AvailabilityRuleRepository;
import com.elec_business.repository.ChargingStationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityRuleBusinessTest {

    @Mock
    private AvailabilityRuleRepository ruleRepo;

    @Mock
    private AvailabilityRuleMapper mapper;

    @Mock
    private TimeSlotBusinessImpl timeSlotService;

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @InjectMocks
    private AvailabilityRuleBusinessImpl availabilityRuleBusiness;

    @Test
    void createRule_Success() {

        String stationId = "station-1";
        String userId = "owner-1";

        User owner = new User();
        owner.setId(userId);

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId(stationId);
        station.setLocation(location);

        // DTO
        AvailabilityRuleDto dto = new AvailabilityRuleDto();
        dto.setStationId(stationId);
        dto.setDayOfWeek(1);
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(12, 0));

        // ENTITY retournée par le mapper
        AvailabilityRule rule = new AvailabilityRule();
        rule.setDayOfWeek(1);
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(12, 0));
        rule.setChargingStation(station);

        when(mapper.toEntity(dto)).thenReturn(rule);
        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(ruleRepo.existsOverlappingRule(eq(stationId), any(), any(), any())).thenReturn(false);
        when(ruleRepo.save(any())).thenAnswer(i -> i.getArguments()[0]);

        availabilityRuleBusiness.createRule(dto, owner);

        verify(ruleRepo).save(any());
        verify(timeSlotService).generateTimeSlotsFromAvailabilityRules(any(), any(), anyList());
    }

    @Test
    void createRule_Fail_StationNotFound() {

        AvailabilityRuleDto dto = new AvailabilityRuleDto();
        dto.setStationId("unknown");

        AvailabilityRule rule = new AvailabilityRule();
        ChargingStation station = new ChargingStation();
        station.setId("unknown");
        rule.setChargingStation(station);

        when(mapper.toEntity(dto)).thenReturn(rule);
        when(chargingStationRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class, () ->
                availabilityRuleBusiness.createRule(dto, new User())
        );
    }

    @Test
    void createRule_Fail_NotOwner() {

        User owner = new User(); owner.setId("owner");
        User hacker = new User(); hacker.setId("hacker");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId("s1");
        station.setLocation(location);

        AvailabilityRuleDto dto = new AvailabilityRuleDto();
        dto.setStationId("s1");

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);


        when(mapper.toEntity(dto)).thenReturn(rule);
        when(chargingStationRepository.findById("s1")).thenReturn(Optional.of(station));

        assertThrows(AccessDeniedStationException.class, () ->
                availabilityRuleBusiness.createRule(dto, hacker)
        );

        verify(ruleRepo, never()).save(any());
    }

    @Test
    void createRule_Fail_Overlap() {

        String stationId = "s1";
        User owner = new User(); owner.setId("u1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId(stationId);
        station.setLocation(location);

        AvailabilityRuleDto dto = new AvailabilityRuleDto();
        dto.setStationId(stationId);
        dto.setDayOfWeek(1);
        dto.setStartTime(LocalTime.of(10, 0));
        dto.setEndTime(LocalTime.of(11, 0));

        AvailabilityRule rule = new AvailabilityRule();
        rule.setDayOfWeek(1);
        rule.setStartTime(LocalTime.of(10, 0));
        rule.setEndTime(LocalTime.of(11, 0));
        rule.setChargingStation(station);

        when(mapper.toEntity(dto)).thenReturn(rule);
        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(ruleRepo.existsOverlappingRule(eq(stationId), eq(1), any(), any())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                availabilityRuleBusiness.createRule(dto, owner)
        );

        assertEquals("Une règle existe déjà sur ce créneau horaire pour ce jour !", ex.getMessage());
        verify(ruleRepo, never()).save(any());
    }

    @Test
    void getRules_Success() {

        String stationId = "s1";
        AvailabilityRule rule = new AvailabilityRule();
        AvailabilityRuleDto dto = new AvailabilityRuleDto();

        when(ruleRepo.findByChargingStation_Id(stationId))
                .thenReturn(Collections.singletonList(rule));

        when(mapper.toDtos(anyList()))
                .thenReturn(Collections.singletonList(dto));

        List<AvailabilityRuleDto> result = availabilityRuleBusiness.getRules(stationId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deleteRule_Success() {

        String ruleId = "rule-123";

        availabilityRuleBusiness.deleteRule(ruleId);

        verify(ruleRepo).deleteById(ruleId);
    }
}
