package com.elec_business.service.impl;

import com.elec_business.dto.ChargingStationRequestDto;
import com.elec_business.dto.ChargingStationUpdateRequestDto;
import com.elec_business.model.AppUser;
import com.elec_business.model.ChargingStation;
import com.elec_business.mapper.ChargingStationMapper;
import com.elec_business.mapper.ChargingStationUpdateMapper;
import com.elec_business.repository.ChargingStationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final ChargingStationMapper chargingStationMapper;
    private final ChargingStationUpdateMapper updateMapper;

    public ChargingStation createChargingStation(ChargingStationRequestDto dto, AppUser currentUser) throws AccessDeniedException {
        ChargingStation station = chargingStationMapper.toEntity(dto);

        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this location");
        }

        return chargingStationRepository.save(station);
    }

    public List<ChargingStation> getAllChargingStations() {
        return chargingStationRepository.findAll();
    }

    public ChargingStation getChargingStationById(UUID id) {
        return chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging station not found"));
    }

    public ChargingStation getChargingStationByName(String name) {
        ChargingStation station = chargingStationRepository.findChargingStationByName(name);
        if (station == null) {
            throw new EntityNotFoundException("Charging station not found");
        }
        return station;
    }

    public ChargingStation updateChargingStation(UUID id, ChargingStationUpdateRequestDto dto, AppUser currentUser) throws AccessDeniedException {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging station not found"));

        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update this station"); // peut être remplacé par AccessDeniedException
        }

        updateMapper.updateChargingStationFromDto(dto, station);
        return chargingStationRepository.save(station);
    }

    public void deleteChargingStationById(UUID id) {
        if (!chargingStationRepository.existsById(id)) {
            throw new EntityNotFoundException("Charging station not found");
        }
        chargingStationRepository.deleteById(id);
    }
}
