package com.elec_business.charging_station.service;

import com.elec_business.charging_station.repository.ChargingLocationRepository;
import com.elec_business.charging_station.model.ChargingStation;
import com.elec_business.charging_station.repository.ChargingStationRepository;
import com.elec_business.charging_station.dto.ChargingStationRequestDto;
import com.elec_business.charging_station.dto.ChargingStationUpdateRequestDto;
import com.elec_business.charging_station.mapper.ChargingStationMapper;
import com.elec_business.charging_station.mapper.ChargingStationUpdateMapper;
import com.elec_business.user.model.AppUser;
import com.elec_business.charging_station.model.ChargingLocation;
import com.elec_business.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargingStationService {

    private final ChargingStationRepository chargingStationRepository;
    private final ChargingStationMapper chargingStationMapper;
    private final ChargingStationUpdateMapper updateMapper;
    private final FileStorageService fileStorageService;
    private final ChargingLocationRepository chargingLocationRepository;

    public ChargingStation createChargingStation(ChargingStationRequestDto dto, AppUser currentUser) throws AccessDeniedException {
        ChargingStation station = chargingStationMapper.toEntity(dto);

        ChargingLocation location = chargingLocationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Location non trouvée"));

        station.setLocation(location);

        // Vérifie que l'utilisateur est bien le propriétaire
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this location");
        }

        // Gestion de l'image
        try {
            String imageUrl;

            if (dto.getImage() != null && !dto.getImage().isEmpty()) {
                // Vérifie le type MIME
                boolean isValidImage = fileStorageService.checkMediaType(dto.getImage(), "image");
                if (!isValidImage) {
                    throw new IllegalArgumentException("Le fichier fourni n'est pas une image valide.");
                }

                // Upload + génération de miniature
                imageUrl = fileStorageService.upload(dto.getImage());
            } else {
                imageUrl = "default.png";

            }

            station.setImageUrl(imageUrl);
            station.setCreatedAt(Instant.now());
            return chargingStationRepository.save(station);

        } catch (Exception e) {
            throw new RuntimeException("Could not save charging station", e);
        }

    }

    public List<ChargingStation> getAllChargingStations() {
        return chargingStationRepository.findAll();
    }

    public ChargingStation getChargingStationById(UUID id) {
        return chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Charging station not found"));
    }

    public List<ChargingStation> getByLocationId(UUID id) {
        return chargingStationRepository.findByLocation_Id(id);
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
