package com.elec_business.charging_station.mapper;

import com.elec_business.charging_station.model.ChargingStation;
import com.elec_business.charging_station.dto.ChargingStationRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {
    public abstract ChargingStation toEntity(ChargingStationRequestDto dto);
}
