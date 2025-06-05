package com.elec_business.mapper;

import com.elec_business.dto.ChargingLocationRequestDto;
import com.elec_business.model.ChargingLocation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChargingLocationMapper {
    public abstract ChargingLocation toEntity(ChargingLocationRequestDto dto);
}
