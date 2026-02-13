package com.elec_business.business;

import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingLocation;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ChargingLocationBusiness {
    ChargingLocationResponseDto createChargingLocation(ChargingLocation chargingLocation);
    List<ChargingLocationResponseDto> getAllChargingLocations();
    ChargingLocationResponseDto getChargingLocationById(String  id, User currentUser) throws AccessDeniedException;
    List<ChargingLocationResponseDto> getChargingLocationByUser(User user);
    ChargingLocationResponseDto updateChargingLocation(String  id, ChargingLocationRequestDto locationdto, User currentUser) throws AccessDeniedException;
    void deleteChargingLocation(String  id, User currentUser) throws AccessDeniedException;
}
