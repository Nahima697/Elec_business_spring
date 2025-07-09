package com.elec_business.availability.mapper;

import com.elec_business.availability.dto.TimeSlotRequestDto;
import com.elec_business.availability.model.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotMapper {
     public abstract TimeSlot toEntity(TimeSlotRequestDto dto);
}
