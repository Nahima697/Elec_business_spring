package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.dto.ChargingStationUpdateRequestDto;
import com.elec_business.entity.ChargingStation;
import com.elec_business.controller.dto.ChargingStationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {
    ChargingStation toEntity(ChargingStationRequestDto dto);
    ChargingStation toUpdateEntity(ChargingStationUpdateRequestDto dto);
    @Mapping(source = "location", target = "locationDTO")
    @Mapping(source = "location.user.id", target = "locationDTO.userId")
    ChargingStationResponseDto toDto(ChargingStation entity);
}
