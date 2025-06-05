package com.elec_business.mapper;

import com.elec_business.dto.ChargingLocationResponseDto;
import com.elec_business.model.ChargingLocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingLocationResponseMapper {
    ChargingLocationResponseDto toDto(ChargingLocation chargingLocation);
}
