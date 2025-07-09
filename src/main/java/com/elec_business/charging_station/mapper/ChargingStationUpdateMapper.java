package com.elec_business.charging_station.mapper;

import com.elec_business.charging_station.model.ChargingStation;
import com.elec_business.charging_station.dto.ChargingStationUpdateRequestDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChargingStationUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChargingStationFromDto(ChargingStationUpdateRequestDto dto, @MappingTarget ChargingStation entity);
}
