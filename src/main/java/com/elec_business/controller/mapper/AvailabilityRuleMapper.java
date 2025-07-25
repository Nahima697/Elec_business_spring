package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.entity.AvailabilityRule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityRuleMapper {
    public   abstract AvailabilityRule toEntity(AvailabilityRuleDto dto) ;
    public   abstract AvailabilityRuleDto toDto(AvailabilityRule entity) ;

}
