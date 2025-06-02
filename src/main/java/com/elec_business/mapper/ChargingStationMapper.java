package com.elec_business.mapper;

import com.elec_business.dto.ChargingStationRequestDto;
import com.elec_business.entity.ChargingStation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {
    public abstract ChargingStation toEntity(ChargingStationRequestDto dto);
}
