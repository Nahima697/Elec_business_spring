package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ChargingLocationRequestDto;
import com.elec_business.controller.dto.ChargingLocationResponseDto;
import com.elec_business.entity.ChargingLocation;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ChargingLocationMapper {

    ChargingLocation toEntity(ChargingLocationRequestDto dto);
    ChargingLocation toEntity(ChargingLocationResponseDto dto);
    ChargingLocationResponseDto toDto(ChargingLocation chargingLocation);
    List<ChargingLocationResponseDto> toDtos(List<ChargingLocation> chargingLocations);
}
