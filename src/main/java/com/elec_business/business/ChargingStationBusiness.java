package com.elec_business.business;

import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.entity.User;
import com.elec_business.entity.ChargingStation;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ChargingStationBusiness {
    ChargingStationResponseDto createChargingStation(ChargingStation station, User currentUser, MultipartFile image) throws AccessDeniedException;
    Page<ChargingStationResponseDto> getAllChargingStations(Pageable pageable);
    ChargingStationResponseDto getChargingStationById(String id);
    List<ChargingStationResponseDto> getByLocationId(String id);
    ChargingStationResponseDto getChargingStationByName(String name);
    ChargingStationResponseDto updateChargingStation(String id, ChargingStation station, User currentUser,MultipartFile image) throws AccessDeniedException;
    void deleteChargingStationById(String id,User currentUser) throws AccessDeniedException;
    List<ChargingStationResponseDto> getMyStations(User currentUser);

}
