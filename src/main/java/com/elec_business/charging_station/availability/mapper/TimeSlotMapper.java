package com.elec_business.charging_station.availability.mapper;

import com.elec_business.charging_station.availability.dto.TimeSlotRequestDto;
import com.elec_business.charging_station.availability.model.TimeSlot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  interface TimeSlotMapper {
     public abstract TimeSlot toEntity(TimeSlotRequestDto dto);
}
