package com.elec_business.controller.mapper;

import com.elec_business.entity.ChargingStation;
import com.elec_business.controller.dto.ChargingStationResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationResponseMapper {
    ChargingStationResponseDto toDto(ChargingStation entity);

}
