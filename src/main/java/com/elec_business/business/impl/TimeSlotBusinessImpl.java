package com.elec_business.business.impl;


import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.controller.mapper.TimeSlotResponseMapper;
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

@Service
@RequiredArgsConstructor
public class TimeSlotBusinessImpl implements TimeSlotBusiness {


    private  final TimeSlotRepository timeSlotRepository;

    private final ChargingStationRepository chargingStationRepository;

    @Transactional

    public void addTimeSlot(String stationId, Instant startTime, Instant endTime) {
        // Vérification de l'existence de la station
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station introuvable"));

        // Créer un TimeSlot
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStation(station);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);

        // Sauvegarder le créneau
       timeSlotRepository.save(timeSlot);

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

    public Page<TimeSlot> getAvailableSlots(String stationId, Pageable pageable) {
        return timeSlotRepository.findByStationId(stationId, pageable);
    }

    public Page<TimeSlot> getAvailableSlotsByPeriod(String stationId, Instant startTime, Instant endTime, Pageable pageable) {
        return timeSlotRepository.findAvailableTimeSlotsByPeriod(stationId,startTime,endTime,pageable);
    }

}
