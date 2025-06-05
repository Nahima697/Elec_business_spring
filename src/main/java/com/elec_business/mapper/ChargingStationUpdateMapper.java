package com.elec_business.mapper;

import com.elec_business.dto.ChargingStationUpdateRequestDto;
import com.elec_business.model.ChargingStation;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChargingStationUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChargingStationFromDto(ChargingStationUpdateRequestDto dto, @MappingTarget ChargingStation entity);
}
