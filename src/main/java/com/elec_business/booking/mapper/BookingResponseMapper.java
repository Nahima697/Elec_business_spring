package com.elec_business.booking.mapper;

import com.elec_business.booking.dto.BookingResponseDto;
import com.elec_business.booking.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingResponseMapper {
    BookingResponseDto toDto(Booking booking);
}
