package com.elec_business.controller.mapper;

import com.elec_business.entity.ChargingStation;
import com.elec_business.controller.dto.ChargingStationRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {
    public abstract ChargingStation toEntity(ChargingStationRequestDto dto);
}
