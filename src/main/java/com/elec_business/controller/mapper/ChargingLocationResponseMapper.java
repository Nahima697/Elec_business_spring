package com.elec_business.controller.mapper;

import com.elec_business.entity.ChargingLocation;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingLocationResponseMapper {
    ChargingLocationResponseDto toDto(ChargingLocation chargingLocation);
}
