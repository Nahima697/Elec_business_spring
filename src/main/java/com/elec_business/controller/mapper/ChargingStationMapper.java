package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.dto.ChargingStationUpdateRequestDto;
import com.elec_business.entity.ChargingStation;
import com.elec_business.controller.dto.ChargingStationRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {
    ChargingStation toEntity(ChargingStationRequestDto dto);
    ChargingStation toUpdateEntity(ChargingStationUpdateRequestDto dto);
    ChargingStationResponseDto toDto(ChargingStation entity);
}
