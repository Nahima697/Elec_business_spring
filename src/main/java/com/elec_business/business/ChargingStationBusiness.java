package com.elec_business.business;

import com.elec_business.entity.User;
import com.elec_business.entity.ChargingStation;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

public interface ChargingStationBusiness {
    ChargingStation createChargingStation(ChargingStation station, User currentUser, MultipartFile image) throws AccessDeniedException;
    Page<ChargingStation> getAllChargingStations(Pageable pageable);
    Optional<ChargingStation> getChargingStationById(String id);
    List<ChargingStation> getByLocationId(String id);
    ChargingStation getChargingStationByName(String name);
    ChargingStation updateChargingStation(String id, ChargingStation station, User currentUser,MultipartFile image) throws AccessDeniedException;
    void deleteChargingStationById(String id,User currentUser) throws AccessDeniedException;
    List<ChargingStation> getMyStations(User currentUser);

}
