package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ChargingStationResponseDto;
import com.elec_business.controller.dto.ChargingStationUpdateRequestDto;
import com.elec_business.entity.ChargingStation;
import com.elec_business.controller.dto.ChargingStationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = ReviewMapper.class)
public interface ChargingStationMapper {
    @Mapping(source = "locationId", target = "location.id")
    ChargingStation toEntity(ChargingStationRequestDto dto);
    ChargingStation toUpdateEntity(ChargingStationUpdateRequestDto dto);
    @Mapping(source = "location", target = "locationDTO")
    @Mapping(source = "location.user.id", target = "locationDTO.userId")
    @Mapping(source="reviews",target = "reviewsDTO")
    ChargingStationResponseDto toDto(ChargingStation entity);
}
