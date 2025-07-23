package com.elec_business.charging_station.availability.mapper;

import com.elec_business.charging_station.availability.dto.AvailabilityRuleDto;
import com.elec_business.charging_station.availability.model.AvailabilityRule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityRuleMapper {
    public   abstract AvailabilityRule toEntity(AvailabilityRuleDto dto) ;
    public   abstract AvailabilityRuleDto toDto(AvailabilityRule entity) ;

}
