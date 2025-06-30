package com.elec_business.mapper;

import com.elec_business.dto.ChargingLocationRequestDto;
import com.elec_business.model.AppUser;
import com.elec_business.model.ChargingLocation;
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
