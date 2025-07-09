package com.elec_business.charging_station.mapper;

import com.elec_business.charging_station.dto.ChargingLocationRequestDto;
import com.elec_business.user.model.AppUser;
import com.elec_business.charging_station.model.ChargingLocation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ChargingLocationMapper {

    ChargingLocation toEntity(ChargingLocationRequestDto dto);

    default ChargingLocation toEntityWithUser(ChargingLocationRequestDto dto, AppUser user) {
        ChargingLocation location = toEntity(dto);
        location.setUser(user);
        return location;
    }
}
