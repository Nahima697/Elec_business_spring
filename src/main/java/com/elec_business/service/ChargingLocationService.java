package com.elec_business.service;

import com.elec_business.dto.ChargingLocationRequestDto;
import com.elec_business.model.AppUser;
import com.elec_business.model.ChargingLocation;
import com.elec_business.mapper.ChargingLocationMapper;
import com.elec_business.repository.ChargingLocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargingLocationService {
    private final ChargingLocationMapper chargingLocationMapper;
    private final ChargingLocationRepository chargingLocationRepository;

    public ChargingLocation createChargingLocation(ChargingLocationRequestDto chargingLocationRequestDto, AppUser currentUser) {
        System.out.println("Current User: " + currentUser);
        if (currentUser != null) {
            System.out.println("User ID: " + currentUser.getId());
        } else {
            System.out.println("Current user is null!");
        }
        ChargingLocation chargingLocation = chargingLocationMapper.toEntityWithUser(chargingLocationRequestDto,currentUser) ;
        try {

            chargingLocationRepository.save(chargingLocation);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return chargingLocation;
    }

    public List<ChargingLocation> getAllChargingLocations() {
        return chargingLocationRepository.findAll();
    }

    public ChargingLocation getChargingLocationById(UUID id) {
        try {
            return chargingLocationRepository.findChargingLocationById(id);
        }
        catch (EntityNotFoundException e) {
            return null;
        }
    }

    public ChargingLocation getChargingLocationByName(String name) {
        return chargingLocationRepository.findChargingLocationByName(name);
    }

    public ChargingLocation updateChargingLocation(UUID id, ChargingLocationRequestDto chargingLocationRequestDto, AppUser currentUser) {
        try {
            ChargingLocation chargingLocation = chargingLocationMapper.toEntityWithUser(chargingLocationRequestDto,currentUser) ;
            chargingLocationRepository.save(chargingLocation);
            return chargingLocation;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }
}
