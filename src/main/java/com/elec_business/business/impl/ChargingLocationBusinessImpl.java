package com.elec_business.business.impl;

import com.elec_business.business.ChargingLocationBusiness;
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

    public ChargingLocation createChargingLocation(ChargingLocation chargingLocation) {

        return chargingLocation;
    }

    public List<ChargingLocation> getAllChargingLocations() {
        return chargingLocationRepository.findAll();
    }

    public ChargingLocation getChargingLocationById(String id, User currentUser) throws AccessDeniedException {
        ChargingLocation location = chargingLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found"));

        if (!location.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this location");
        }

        return location;
    }


    public List<ChargingLocation> getChargingLocationByUser(User user) {

        return chargingLocationRepository.findByUser(user);
    }


    public ChargingLocation updateChargingLocation(String id, ChargingLocation location, User currentUser) throws AccessDeniedException {
        ChargingLocation existingLocation = chargingLocationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging location not found"));

        if (!existingLocation.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this location.");
        }

        existingLocation.setAddressLine(location.getAddressLine());
        existingLocation.setCity(location.getCity());
        existingLocation.setPostalCode(location.getPostalCode());
        existingLocation.setCountry(location.getCountry());
        existingLocation.setName(location.getName());

        return chargingLocationRepository.save(existingLocation);
    }


    public void deleteChargingLocation(String id, User currentUser) throws AccessDeniedException {
        ChargingLocation location = getChargingLocationById(id, currentUser);
        chargingLocationRepository.delete(location);
    }

}


