package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.entity.AvailabilityRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilityRuleMapper {

    @Mapping(source = "stationId", target = "chargingStation.id")
    @Mapping(source = "dayOfWeek", target = "dayOfWeek")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    AvailabilityRule toEntity(AvailabilityRuleDto dto);

    @Mapping(source = "chargingStation.id", target = "stationId")
    @Mapping(source = "chargingStation.name", target = "stationName")
    AvailabilityRuleDto toDto(AvailabilityRule entity);
}

