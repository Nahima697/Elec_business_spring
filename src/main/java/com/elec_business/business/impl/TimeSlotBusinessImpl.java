package com.elec_business.business.impl;


import com.elec_business.controller.mapper.TimeSlotMapper;
import com.elec_business.controller.mapper.TimeSlotResponseMapper;
import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.TimeSlot;
import com.elec_business.repository.TimeSlotRepository;
import com.elec_business.entity.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeSlotBusinessImpl {


    private  final TimeSlotRepository timeSlotRepository;

    private final ChargingStationRepository chargingStationRepository;
    private final TimeSlotMapper timeSlotMapper;
    private final TimeSlotResponseMapper timeSlotResponseMapper;

    @Transactional

    public TimeSlotResponseDto addTimeSlot(UUID stationId, Instant startTime, Instant endTime) {
        // Vérification de l'existence de la station
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station introuvable"));

        // Créer un TimeSlot
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStation(station);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);

        // Sauvegarder le créneau
        TimeSlot savedTimeSlot = timeSlotRepository.save(timeSlot);

        // Mapper en DTO
        return new TimeSlotResponseDto(
                savedTimeSlot.getId(),
                savedTimeSlot.getStation().getId(),   // Renvoie juste l'ID de la station
                savedTimeSlot.getStation().getName(), // Nom de la station
                savedTimeSlot.getStartTime(),
                savedTimeSlot.getEndTime()
        );
    }

    public void generateTimeSlotsFromAvailabilityRules(LocalDate startDate, LocalDate endDate, List<AvailabilityRule> rules) {
        List<TimeSlot> generatedSlots = new ArrayList<>();

        for (AvailabilityRule rule : rules) {
            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                if (currentDate.getDayOfWeek().getValue() == rule.getDayOfWeek()) {
                    ZonedDateTime zonedStart = currentDate.atTime(rule.getStartTime()).atZone(ZoneId.systemDefault());
                    ZonedDateTime zonedEnd = currentDate.atTime(rule.getEndTime()).atZone(ZoneId.systemDefault());

                    TimeSlot slot = new TimeSlot();
                    slot.setStation(rule.getChargingStation());
                    slot.setStartTime(zonedStart.toInstant());
                    slot.setEndTime(zonedEnd.toInstant());

                    generatedSlots.add(slot);
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        timeSlotRepository.saveAll(generatedSlots);
    }

    public void purgeOldTimeSlots() {
        timeSlotRepository.deleteByStartTimeBefore(Instant.now());
    }

    public Page<TimeSlotResponseDto> getAvailableSlots(UUID stationId, Pageable pageable) {
        return timeSlotRepository.findByStationId(stationId, pageable)
                .map(timeSlotResponseMapper::toDto);
    }

    public Page<TimeSlotResponseDto> getAvailableSlotsByPeriode(UUID stationId, Instant startTime, Instant endTime, Pageable pageable) {
        return timeSlotRepository.findAvailableTimeSlotsByPeriod(stationId,startTime,endTime,pageable)
                .map(timeSlotResponseMapper::toDto);
    }

}
