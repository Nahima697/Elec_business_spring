package com.elec_business.controller;

import com.elec_business.dto.ChargingStationRequestDto;
import com.elec_business.dto.ChargingStationResponseDto;
import com.elec_business.dto.ChargingStationUpdateRequestDto;
import com.elec_business.model.AppUser;
import com.elec_business.model.ChargingStation;
import com.elec_business.mapper.ChargingStationResponseMapper;
import com.elec_business.service.impl.ChargingStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/charging_stations")
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingStationResponseDto addChargingStation(@Valid @RequestBody ChargingStationRequestDto dto,
                                                         @AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        ChargingStation createdStation = chargingStationService.createChargingStation(dto, currentUser);
        return chargingStationResponseMapper.toDto(createdStation);
    }

    @PatchMapping("/charging_stations/{id}")
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
