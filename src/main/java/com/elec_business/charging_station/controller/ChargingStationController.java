package com.elec_business.charging_station.controller;

import com.elec_business.charging_station.dto.ChargingStationRequestDto;
import com.elec_business.charging_station.dto.ChargingStationResponseDto;
import com.elec_business.charging_station.dto.ChargingStationUpdateRequestDto;
import com.elec_business.charging_station.mapper.ChargingStationResponseMapper;
import com.elec_business.charging_station.model.ChargingStation;
import com.elec_business.charging_station.service.ChargingStationService;
import com.elec_business.user.model.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChargingStationController {

    private final ChargingStationService chargingStationService;
    private final ChargingStationResponseMapper chargingStationResponseMapper;

    @PostMapping(value = "/charging_stations",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingStationResponseDto addChargingStation(@Valid   @ModelAttribute ChargingStationRequestDto dto,
                                                         @AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        ChargingStation createdStation = chargingStationService.createChargingStation(dto, currentUser);
        return chargingStationResponseMapper.toDto(createdStation);
    }

    @PutMapping("/charging_stations/{id}")
    public ChargingStationResponseDto updateStation(@PathVariable UUID id,
                                                    @RequestBody @Valid ChargingStationUpdateRequestDto dto,
                                                    @AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        ChargingStation updated = chargingStationService.updateChargingStation(id, dto, currentUser);
        return chargingStationResponseMapper.toDto(updated);
    }

    @GetMapping("/charging_stations")
    public List<ChargingStationResponseDto> getAllChargingStations() {
        return chargingStationService.getAllChargingStations()
                .stream()
                .map(chargingStationResponseMapper::toDto)
                .toList();
    }

    @GetMapping("/charging_stations/location/{locationId}")
    public List<ChargingStationResponseDto> getChargingStationsByUser(@PathVariable UUID locationId) {
        return chargingStationService.getByLocationId(locationId)
                .stream()
                .map(chargingStationResponseMapper::toDto)
                .toList();
    }

    @GetMapping("/charging_stations/{id}")
    public ChargingStationResponseDto getStation(@PathVariable UUID id) {
        ChargingStation station = chargingStationService.getChargingStationById(id);
        return chargingStationResponseMapper.toDto(station);
    }

    @DeleteMapping("/charging_stations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChargingStation(@PathVariable UUID id) {
        chargingStationService.deleteChargingStationById(id);
    }
}
