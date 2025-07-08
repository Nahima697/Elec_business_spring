package com.elec_business.mapper;

import com.elec_business.dto.AvailabilityRuleDto;
import com.elec_business.model.AvailabilityRule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityRuleMapper {
    public   abstract AvailabilityRule toEntity(AvailabilityRuleDto dto) ;
    public   abstract AvailabilityRuleDto toDto(AvailabilityRule entity) ;

}
