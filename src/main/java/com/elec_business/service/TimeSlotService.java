package com.elec_business.service;


import com.elec_business.dto.TimeSlotResponseDto;
import com.elec_business.mapper.TimeSlotMapper;
import com.elec_business.mapper.TimeSlotResponseMapper;
import com.elec_business.model.ChargingStation;
import com.elec_business.model.TimeSlot;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.TimeSlotRepository;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private TimeSlotMapper timeSlotMapper; // Le mapper qui va transformer le DTO en entité
    private TimeSlotResponseMapper timeSlotResponseMapper;

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
}
