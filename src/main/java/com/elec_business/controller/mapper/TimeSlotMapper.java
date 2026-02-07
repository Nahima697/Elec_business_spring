package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.TimeSlotRequestDto;
import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.entity.TimeSlot;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public  interface  TimeSlotMapper {
     TimeSlot toEntity(TimeSlotRequestDto dto);
     TimeSlotResponseDto toDto(TimeSlot entity);
     List<TimeSlotResponseDto> toDtoList(List<TimeSlot> entities);
     default Page<TimeSlotResponseDto> toDtoPage(Page<TimeSlot> entityPage) {
          return entityPage.map(this::toDto);
     }
}
