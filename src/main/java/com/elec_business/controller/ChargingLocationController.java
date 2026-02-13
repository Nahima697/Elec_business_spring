package com.elec_business.controller;

import com.elec_business.business.ChargingLocationBusiness;
import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import com.elec_business.controller.mapper.ChargingLocationMapper;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingLocation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChargingLocationController {

    private final ChargingLocationBusiness chargingLocationBusiness;
    private final ChargingLocationMapper chargingLocationMapper;

    @PostMapping("/charging_locations")
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingLocationResponseDto addLocation(
            @Valid @RequestBody ChargingLocationRequestDto chargingLocationRequestDto,
            @AuthenticationPrincipal User currentUser) {
        ChargingLocation location = chargingLocationMapper.toEntity(chargingLocationRequestDto);
        location.setUser(currentUser);
        return  chargingLocationBusiness.createChargingLocation(location);

    }

    @PutMapping("/charging_locations/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChargingLocationResponseDto updateLocation(@PathVariable String id, @Valid @RequestBody ChargingLocationRequestDto chargingLocationRequestDto,
                                                      @AuthenticationPrincipal User currentUser) throws AccessDeniedException {
      return chargingLocationBusiness.updateChargingLocation(id,chargingLocationRequestDto,currentUser);

    }

    @GetMapping("/charging_locations")
    @ResponseStatus(HttpStatus.OK)
    public List<ChargingLocationResponseDto> getAllLocations( ) {
        return chargingLocationBusiness.getAllChargingLocations();

    }

    @GetMapping("/charging_locations/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChargingLocationResponseDto getLocation(@PathVariable String  id,@AuthenticationPrincipal User currentUser) throws AccessDeniedException {
            return chargingLocationBusiness.getChargingLocationById(id,currentUser);
    }

    @GetMapping("/charging_locations/user")
    @ResponseStatus(HttpStatus.OK)
    public List<ChargingLocationResponseDto> getLocationsByUserId(@AuthenticationPrincipal User currentUser) {
        return chargingLocationBusiness.getChargingLocationByUser(currentUser);

    }


    @DeleteMapping("/charging_locations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChargingLocation(@PathVariable String id,@AuthenticationPrincipal User currentUser) throws AccessDeniedException {
        chargingLocationBusiness.deleteChargingLocation(id,currentUser);
    }
}
