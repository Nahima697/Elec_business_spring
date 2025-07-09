package com.elec_business.charging_station.mapper;

import com.elec_business.charging_station.model.ChargingLocation;
import com.elec_business.charging_station.dto.ChargingLocationResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingLocationResponseMapper {
    ChargingLocationResponseDto toDto(ChargingLocation chargingLocation);
}
