package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.entity.AppUser;
import com.elec_business.entity.ChargingLocation;
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
