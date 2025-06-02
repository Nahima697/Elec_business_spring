package com.elec_business.mapper;

import com.elec_business.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingResponseMapper {
    BookingResponseDto toDto(Booking booking);
}
