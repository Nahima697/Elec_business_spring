package com.elec_business.controller.mapper;

import com.elec_business.entity.ChargingStation;
import com.elec_business.controller.dto.ChargingStationUpdateRequestDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChargingStationUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChargingStationFromDto(ChargingStationUpdateRequestDto dto, @MappingTarget ChargingStation entity);
}
