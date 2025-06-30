package com.elec_business.mapper;

import com.elec_business.dto.TimeSlotRequestDto;
import com.elec_business.dto.TimeSlotResponseDto;
import com.elec_business.model.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotResponseMapper {
     public abstract TimeSlotResponseDto toDto(TimeSlot timeSlot);
}
