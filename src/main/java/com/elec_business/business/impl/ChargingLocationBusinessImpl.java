package com.elec_business.business.impl;

import com.elec_business.business.ChargingLocationBusiness;
import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import com.elec_business.controller.mapper.ChargingLocationMapper;
import com.elec_business.entity.User;
import com.elec_business.repository.ChargingLocationRepository;
import com.elec_business.entity.ChargingLocation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingLocationBusinessImpl implements ChargingLocationBusiness {
    private final ChargingLocationRepository chargingLocationRepository;
    private final ChargingLocationMapper chargingLocationMapper;

    public ChargingLocationResponseDto createChargingLocation(ChargingLocation chargingLocation) {

        return chargingLocationMapper.toDto(chargingLocationRepository.save(chargingLocation));
    }

    public List<ChargingLocationResponseDto> getAllChargingLocations() {
        return chargingLocationMapper.toDtos(chargingLocationRepository.findAll());
    }

    public ChargingLocationResponseDto getChargingLocationById(String id, User currentUser) throws AccessDeniedException {
        ChargingLocation location = chargingLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        if (!location.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this location");
        }

        return chargingLocationMapper.toDto(location);
    }


    public List<ChargingLocationResponseDto> getChargingLocationByUser(User user) {

        return chargingLocationMapper.toDtos(chargingLocationRepository.findByUser(user));
    }


    public ChargingLocationResponseDto updateChargingLocation(String id, ChargingLocationRequestDto dto, User currentUser) throws AccessDeniedException {
        ChargingLocation existingLocation = chargingLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging location not found"));

        if (!existingLocation.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this location.");
        }

        ChargingLocation location = chargingLocationMapper.toEntity(dto);
        existingLocation.setAddressLine(location.getAddressLine());
        existingLocation.setCity(location.getCity());
        existingLocation.setPostalCode(location.getPostalCode());
        existingLocation.setCountry(location.getCountry());
        existingLocation.setName(location.getName());

        return chargingLocationMapper.toDto(chargingLocationRepository.save(existingLocation));
    }


    public void deleteChargingLocation(String id, User currentUser) throws AccessDeniedException {
        ChargingLocation location =chargingLocationMapper.toEntity(getChargingLocationById(id, currentUser));
        chargingLocationRepository.delete(location);
    }

}


