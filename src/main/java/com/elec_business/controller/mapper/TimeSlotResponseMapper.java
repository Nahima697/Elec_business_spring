package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.entity.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotResponseMapper {
     public abstract TimeSlotResponseDto toDto(TimeSlot timeSlot);
}
