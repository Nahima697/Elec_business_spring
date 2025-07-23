package com.elec_business.charging_station.controller;

import com.elec_business.charging_station.dto.ChargingLocationRequestDto;
import com.elec_business.charging_station.dto.ChargingLocationResponseDto;
import com.elec_business.charging_station.mapper.ChargingLocationResponseMapper;
import com.elec_business.charging_station.service.ChargingLocationService;
import com.elec_business.user.model.AppUser;
import com.elec_business.charging_station.model.ChargingLocation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChargingLocationController {

    private final ChargingLocationService chargingLocationService;
    private final ChargingLocationResponseMapper chargingLocationResponseMapper;

    @PostMapping("/charging_locations")
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingLocationResponseDto addLocation(
            @Valid @RequestBody ChargingLocationRequestDto chargingLocationRequestDto,
            @AuthenticationPrincipal AppUser currentUser) {
        ChargingLocation createdLocation = chargingLocationService.createChargingLocation(chargingLocationRequestDto,currentUser);
        return chargingLocationResponseMapper.toDto(createdLocation);
    }

    @PutMapping("/charging_locations/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChargingLocationResponseDto updateLocation(@PathVariable UUID id, @Valid @RequestBody ChargingLocationRequestDto chargingLocationRequestDto,
                                                      @AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        ChargingLocation updatedLocation= chargingLocationService.updateChargingLocation(id,chargingLocationRequestDto,currentUser);
        return chargingLocationResponseMapper.toDto(updatedLocation);
    }

    @GetMapping("/charging_locations")
    @ResponseStatus(HttpStatus.OK)
    public List<ChargingLocationResponseDto> getAllLocations( ) {
        return chargingLocationService.getAllChargingLocations()
                .stream()
                .map(chargingLocationResponseMapper::toDto)
                .toList() ;
    }

    @GetMapping("/charging_locations/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChargingLocationResponseDto getLocation(@PathVariable UUID id,@AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
            return chargingLocationResponseMapper.toDto(chargingLocationService.getChargingLocationById(id,currentUser));
    }

    @GetMapping("/charging_locations/user")
    @ResponseStatus(HttpStatus.OK)
    public List<ChargingLocationResponseDto> getLocationsByUserId(@AuthenticationPrincipal AppUser currentUser) {
        return chargingLocationService.getChargingLocationByUser(currentUser).stream()
                .map(chargingLocationResponseMapper::toDto)
                .toList();
    }


    @DeleteMapping("/charging_locations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChargingLocation(@PathVariable UUID id,@AuthenticationPrincipal AppUser currentUser) throws AccessDeniedException {
        chargingLocationService.deleteChargingLocation(id,currentUser);
    }
}
