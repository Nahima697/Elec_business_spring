package com.elec_business.controller;

import com.elec_business.dto.ChargingStationRequestDto;
import com.elec_business.dto.ChargingStationResponseDto;
import com.elec_business.entity.AppUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChargingStationController {

    @PostMapping("/charging_stations")
    @ResponseStatus(HttpStatus.CREATED)
    public ChargingStationRequestDto addChargingStation(@Valid @RequestBody ChargingStationRequestDto chargingStationRequestDto,
                                                        @AuthenticationPrincipal AppUser currentUser) {
        return chargingStationRequestDto;// injecter service
    }
}
