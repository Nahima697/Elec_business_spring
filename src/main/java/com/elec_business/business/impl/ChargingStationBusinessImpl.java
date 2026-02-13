package com.elec_business.business.impl;

import com.elec_business.business.ChargingStationBusiness;
import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.mapper.ChargingStationMapper;
import com.elec_business.repository.ChargingLocationRepository;
import com.elec_business.entity.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingLocation;
import com.elec_business.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DialectOverride;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;
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
    private final ChargingStationMapper chargingStationMapper;

    @Override
    public ChargingStationResponseDto createChargingStation(ChargingStation station, User currentUser, MultipartFile image) throws AccessDeniedException {
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
            if (image != null && !image.isEmpty()) {

                boolean isValidImage = fileStorageService.checkMediaType(image, "image");
                if (!isValidImage) {
                    throw new IllegalArgumentException("Le fichier fourni n'est pas une image valide.");
                }

                imageUrl = fileStorageService.upload(image);
            } else {
                imageUrl = "default.png";
            }

            station.setImageUrl(imageUrl);
            station.setCreatedAt(Instant.now());
           return  chargingStationMapper.toDto(chargingStationRepository.save(station));

        } catch (InvalidMediaTypeException e ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid format, image required");
        }

    }

    @Override
    public Page<ChargingStationResponseDto> getAllChargingStations(Pageable pageable) {
        return chargingStationRepository
                .findAll(pageable)
                .map(chargingStationMapper::toDto);
    }

    @Override
    public ChargingStationResponseDto getChargingStationById(String id) {
        ChargingStation station =chargingStationRepository.findByIdWithDetails(id);
        if(station == null) {
            throw new EntityNotFoundException("Charging station not found");
        }
        return  chargingStationMapper.toDto(station);
    }

    @Override
    @Transactional
    public List<ChargingStationResponseDto> getByLocationId(String id)  {
        if (!chargingLocationRepository.existsById(id)) {
            throw new EntityNotFoundException("Lieu introuvable");
        }
        return chargingStationMapper.toDtos(chargingStationRepository.findByLocation_Id(id));
    }

    public ChargingStationResponseDto getChargingStationByName(String name) {
        ChargingStation station = chargingStationRepository.findChargingStationByName(name);
        if (station == null) {
            throw new EntityNotFoundException(ERR_STATION_NOT_FOUND);
        }
        return chargingStationMapper.toDto(station);
    }

    @Override
    @Transactional
    public List<ChargingStationResponseDto> getMyStations(User currentUser) {
        return chargingStationMapper.toDtos(chargingStationRepository.findByOwnerEmail(currentUser.getEmail()));
    }

    @Override
    @Transactional
    public ChargingStationResponseDto updateChargingStation(String id, ChargingStation stationChanges, User currentUser, MultipartFile image) throws AccessDeniedException {

        ChargingStation existingStation = chargingStationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_STATION_NOT_FOUND));

        if (!existingStation.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to update this station");
        }

        existingStation.setName(stationChanges.getName());
        existingStation.setDescription(stationChanges.getDescription());
        existingStation.setPowerKw(stationChanges.getPowerKw());
        existingStation.setPrice(stationChanges.getPrice());

        if(stationChanges.getLocation() != null) {
            existingStation.setLocation(stationChanges.getLocation());
        }

        if (image != null && !image.isEmpty()) {
            try {
                boolean isValidImage = fileStorageService.checkMediaType(image, "image");
                if (!isValidImage) {
                    throw new IllegalArgumentException("Le fichier fourni n'est pas une image valide.");
                }
                String newImageUrl = fileStorageService.upload(image);
                existingStation.setImageUrl(newImageUrl);

            } catch (InvalidMediaTypeException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format");
            }
        }
        return chargingStationMapper.toDto(chargingStationRepository.save(existingStation));
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
