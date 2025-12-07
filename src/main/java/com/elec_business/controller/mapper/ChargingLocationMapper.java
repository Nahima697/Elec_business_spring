package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import com.elec_business.entity.ChargingLocation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ChargingLocationMapper {

    ChargingLocation toEntity(ChargingLocationRequestDto dto);
    ChargingLocationResponseDto toDto(ChargingLocation chargingLocation);
}
