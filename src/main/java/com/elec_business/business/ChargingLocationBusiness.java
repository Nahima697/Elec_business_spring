package com.elec_business.business;

import com.elec_business.entity.User;
import com.elec_business.entity.ChargingLocation;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ChargingLocationBusiness {
    ChargingLocation createChargingLocation(ChargingLocation chargingLocation);
    List<ChargingLocation> getAllChargingLocations();
    ChargingLocation getChargingLocationById(String  id, User currentUser) throws AccessDeniedException;
    List<ChargingLocation> getChargingLocationByUser(User user);
    ChargingLocation updateChargingLocation(String  id, ChargingLocation location, User currentUser) throws AccessDeniedException;
    void deleteChargingLocation(String  id, User currentUser) throws AccessDeniedException;
}
