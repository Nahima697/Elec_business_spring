package com.elec_business.charging_station.service;

import com.elec_business.charging_station.repository.ChargingLocationRepository;
import com.elec_business.charging_station.dto.ChargingLocationRequestDto;
import com.elec_business.charging_station.mapper.ChargingLocationMapper;
import com.elec_business.user.jwt.JwtUtil;
import com.elec_business.user.model.AppUser;
import com.elec_business.charging_station.model.ChargingLocation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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

    public ChargingLocation getChargingLocationById(UUID id, AppUser currentUser) throws AccessDeniedException {
        ChargingLocation location = chargingLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        if (!location.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this location");
        }

        return location;
    }


    public List<ChargingLocation> getChargingLocationByUser(AppUser user) {

        return chargingLocationRepository.findByUser(user);
    }


    public ChargingLocation updateChargingLocation(UUID id, ChargingLocationRequestDto dto, AppUser currentUser) throws AccessDeniedException {
        ChargingLocation existingLocation = chargingLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging location not found"));

        if (!existingLocation.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this location.");
        }

        existingLocation.setAddressLine(dto.getAddressLine());
        existingLocation.setCity(dto.getCity());
        existingLocation.setPostalCode(dto.getPostalCode());
        existingLocation.setCountry(dto.getCountry());
        existingLocation.setName(dto.getName());

        return chargingLocationRepository.save(existingLocation);
    }


    public void deleteChargingLocation(UUID id, AppUser currentUser) throws AccessDeniedException {
        ChargingLocation location = getChargingLocationById(id, currentUser);
        chargingLocationRepository.delete(location);
    }

}


