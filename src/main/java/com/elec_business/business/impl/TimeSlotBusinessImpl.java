package com.elec_business.business.impl;


import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.controller.mapper.TimeSlotResponseMapper;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.TimeSlot;
import com.elec_business.repository.TimeSlotRepository;
import com.elec_business.entity.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotBusinessImpl implements TimeSlotBusiness {


    private  final TimeSlotRepository timeSlotRepository;

    private final ChargingStationRepository chargingStationRepository;

    @Transactional

    public void addTimeSlot(String stationId, LocalDateTime startTime, LocalDateTime endTime) {
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

    @Transactional
    public void setTimeSlotAvailability(String stationId, LocalDateTime startTime, LocalDateTime endTime) {
        // Récupérer tous les slots dans la plage donnée
        List<TimeSlot> slots = timeSlotRepository.findSlotsInRange(stationId, startTime, endTime, "[]");

        for (TimeSlot slot : slots) {
            slot.setIsAvailable(false);
        }

        if (!slots.isEmpty()) {
            timeSlotRepository.saveAll(slots);
        }
    }


    public void generateTimeSlotsFromAvailabilityRules(LocalDate startDate, LocalDate endDate, List<AvailabilityRule> rules) {
        List<TimeSlot> generatedSlots = new ArrayList<>();

        for (AvailabilityRule rule : rules) {
            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                if (currentDate.getDayOfWeek().getValue() == rule.getDayOfWeek()) {
                    LocalDateTime startDateTime = currentDate.atTime(rule.getStartTime());
                    LocalDateTime endDateTime = currentDate.atTime(rule.getEndTime());

                    TimeSlot slot = new TimeSlot();
                    slot.setStation(rule.getChargingStation());
                    slot.setStartTime(startDateTime);
                    slot.setEndTime(endDateTime);
                    slot.setIsAvailable(true); // facultatif si déjà true par défaut

                    slot.setAvailability(Range.closed(startDateTime, endDateTime));

                    generatedSlots.add(slot);
                }

                currentDate = currentDate.plusDays(1);
            }
        }

        timeSlotRepository.saveAll(generatedSlots);
    }


    public void purgeOldTimeSlots() {
        timeSlotRepository.deleteByStartTimeBefore(LocalDateTime.now());
    }

    public Page<TimeSlot> getAvailableSlots(String stationId, Pageable pageable) {
        return timeSlotRepository.findByStationId(stationId, pageable);
    }


    public Page<TimeSlot> getAvailableSlotsByPeriod(String stationId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return timeSlotRepository.findAvailableSlotsPage(stationId, startTime, endTime, pageable);
    }

}
