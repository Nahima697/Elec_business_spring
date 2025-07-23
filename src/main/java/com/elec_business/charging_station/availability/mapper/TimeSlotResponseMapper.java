package com.elec_business.charging_station.availability.mapper;

import com.elec_business.charging_station.availability.dto.TimeSlotResponseDto;
import com.elec_business.charging_station.availability.model.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotResponseMapper {
     public abstract TimeSlotResponseDto toDto(TimeSlot timeSlot);
}
