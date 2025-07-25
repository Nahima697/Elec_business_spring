package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.TimeSlotRequestDto;
import com.elec_business.entity.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotMapper {
     public abstract TimeSlot toEntity(TimeSlotRequestDto dto);
}
