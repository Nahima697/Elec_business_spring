package com.elec_business.business.impl;

import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.business.exception.StationNotFoundException;
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
        // ARRANGE
        String stationId = "station-1";
        String userId = "owner-1";

        User owner = new User();
        owner.setId(userId);

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId(stationId);
        station.setLocation(location);

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);
        rule.setDayOfWeek(1);
        rule.setStartTime(LocalTime.of(8, 0));
        rule.setEndTime(LocalTime.of(12, 0));

        // Mocks
        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(ruleRepo.existsOverlappingRule(eq(stationId), any(), any(), any())).thenReturn(false);
        // Important : save retourne l'objet passé
        when(ruleRepo.save(any(AvailabilityRule.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        availabilityRuleBusiness.createRule(rule, owner);

        // ASSERT
        verify(ruleRepo).save(rule);
        // On vérifie que la génération des slots est appelée
        verify(timeSlotService).generateTimeSlotsFromAvailabilityRules(any(), any(), anyList());
    }

    @Test
    void createRule_Fail_StationNotFound() {
        User user = new User();
        ChargingStation station = new ChargingStation();
        station.setId("unknown");

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);

        when(chargingStationRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(StationNotFoundException.class, () ->
                availabilityRuleBusiness.createRule(rule, user)
        );
    }

    @Test
    void createRule_Fail_NotOwner() {
        // ARRANGE
        User owner = new User(); owner.setId("owner");
        User hacker = new User(); hacker.setId("hacker");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId("s1");
        station.setLocation(location);

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);

        when(chargingStationRepository.findById("s1")).thenReturn(Optional.of(station));

        // ACT & ASSERT
        assertThrows(AccessDeniedStationException.class, () ->
                availabilityRuleBusiness.createRule(rule, hacker)
        );

        verify(ruleRepo, never()).save(any());
    }

    @Test
    void createRule_Fail_Overlap() {
        // ARRANGE
        String stationId = "s1";
        User owner = new User(); owner.setId("u1");

        ChargingLocation location = new ChargingLocation();
        location.setUser(owner);

        ChargingStation station = new ChargingStation();
        station.setId(stationId);
        station.setLocation(location);

        AvailabilityRule rule = new AvailabilityRule();
        rule.setChargingStation(station);
        rule.setDayOfWeek(1);
        rule.setStartTime(LocalTime.of(10, 0));
        rule.setEndTime(LocalTime.of(11, 0));

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(ruleRepo.existsOverlappingRule(eq(stationId), eq(1), any(), any())).thenReturn(true);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                availabilityRuleBusiness.createRule(rule, owner)
        );

        assertEquals("Une règle existe déjà sur ce créneau horaire pour ce jour !", ex.getMessage());
        verify(ruleRepo, never()).save(any());
    }

    @Test
    void getRules_Success() {
        String stationId = "s1";
        AvailabilityRule rule = new AvailabilityRule();
        when(ruleRepo.findByChargingStation_Id(stationId)).thenReturn(Collections.singletonList(rule));

        List<AvailabilityRule> result = availabilityRuleBusiness.getRules(stationId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deleteRule_Success() {
        String ruleId = "rule-123";
        doNothing().when(ruleRepo).deleteById(ruleId);

        availabilityRuleBusiness.deleteRule(ruleId);

        verify(ruleRepo).deleteById(ruleId);
    }
}