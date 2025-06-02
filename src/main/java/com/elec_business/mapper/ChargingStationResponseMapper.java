package com.elec_business.mapper;

import com.elec_business.dto.ChargingStationResponseDto;
import com.elec_business.entity.ChargingStation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationResponseMapper {
    ChargingStationResponseDto toDto(ChargingStation entity);

}
