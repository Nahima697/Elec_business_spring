package com.elec_business.availability.mapper;

import com.elec_business.availability.dto.TimeSlotResponseDto;
import com.elec_business.availability.model.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotResponseMapper {
     public abstract TimeSlotResponseDto toDto(TimeSlot timeSlot);
}
