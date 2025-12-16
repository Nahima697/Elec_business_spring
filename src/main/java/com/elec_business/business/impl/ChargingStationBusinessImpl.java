package com.elec_business.business.impl;

import com.elec_business.business.ChargingStationBusiness;
import com.elec_business.repository.ChargingLocationRepository;
import com.elec_business.entity.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingLocation;
import com.elec_business.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingStationBusinessImpl implements ChargingStationBusiness {

    private final ChargingStationRepository chargingStationRepository;
    private final FileStorageService fileStorageService;
    private final ChargingLocationRepository chargingLocationRepository;
    private static final String ERR_STATION_NOT_FOUND = "Charging station not found";


    public ChargingStation createChargingStation(ChargingStation station, User currentUser, MultipartFile image) throws AccessDeniedException {
        ChargingLocation location = chargingLocationRepository.findById(station.getLocation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Location non trouvée"));

        station.setLocation(location);

        // Vérifie que l'utilisateur est bien le propriétaire
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this location");
        }

        // Gestion de l'image
        try {
            String imageUrl;

            if (station.getImageUrl() != null && !station.getImageUrl().isEmpty()) {
                // Vérifie le type MIME
                boolean isValidImage = fileStorageService.checkMediaType(image, "image");
                if (!isValidImage) {
                    throw new IllegalArgumentException("Le fichier fourni n'est pas une image valide.");
                }

                // Upload + génération de miniature
                imageUrl = fileStorageService.upload(image);
            } else {
                imageUrl = "default.png";

            }

            station.setImageUrl(imageUrl);
            station.setCreatedAt(Instant.now());
           return chargingStationRepository.save(station);

        } catch (InvalidMediaTypeException e ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid format, image required");
        }

    }

    public Page<ChargingStation> getAllChargingStations(Pageable pageable) {
        return chargingStationRepository.findAll(pageable);
    }

    public ChargingStation getChargingStationById(String id) {
        return chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_STATION_NOT_FOUND));
    }

    public List<ChargingStation> getByLocationId(String id) {
        return chargingStationRepository.findByLocation_Id(id);
    }

    public ChargingStation getChargingStationByName(String name) {
        ChargingStation station = chargingStationRepository.findChargingStationByName(name);
        if (station == null) {
            throw new EntityNotFoundException(ERR_STATION_NOT_FOUND);
        }
        return station;
    }

    public ChargingStation updateChargingStation(String id, ChargingStation station, User currentUser) throws AccessDeniedException {
        ChargingStation updateStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_STATION_NOT_FOUND));

        if (!updateStation.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update this station");
        }


        updateStation.setName(station.getName());
        updateStation.setDescription(station.getDescription());
        updateStation.setLocation(station.getLocation());
        updateStation.setCreatedAt(Instant.now());
        updateStation.setPowerKw(station.getPowerKw());

        updateStation.setPrice(station.getPrice());
        return chargingStationRepository.save(updateStation);
    }

    public void deleteChargingStationById(String id, User currentUser) throws AccessDeniedException {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_STATION_NOT_FOUND));

        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this station");
        }

        chargingStationRepository.delete(station);
    }

}
