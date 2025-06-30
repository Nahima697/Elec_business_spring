package com.elec_business.mapper;

import com.elec_business.dto.ChargingStationRequestDto;
import com.elec_business.model.ChargingStation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {
    public abstract ChargingStation toEntity(ChargingStationRequestDto dto);
}
