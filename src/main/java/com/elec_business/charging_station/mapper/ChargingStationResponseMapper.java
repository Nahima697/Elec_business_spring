package com.elec_business.charging_station.mapper;

import com.elec_business.charging_station.model.ChargingStation;
import com.elec_business.charging_station.dto.ChargingStationResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationResponseMapper {
    ChargingStationResponseDto toDto(ChargingStation entity);

}
