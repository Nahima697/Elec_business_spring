package com.elec_business.business;

import com.elec_business.entity.User;
import com.elec_business.entity.ChargingStation;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ChargingStationBusiness {
    ChargingStation createChargingStation(ChargingStation station, User currentUser, MultipartFile image) throws AccessDeniedException;
    List<ChargingStation> getAllChargingStations();
    ChargingStation getChargingStationById(String id);
    List<ChargingStation> getByLocationId(String id);
    ChargingStation getChargingStationByName(String name);
    ChargingStation updateChargingStation(String id, ChargingStation station, User currentUser) throws AccessDeniedException;
    void deleteChargingStationById(String id,User currentUser) throws AccessDeniedException;

}
