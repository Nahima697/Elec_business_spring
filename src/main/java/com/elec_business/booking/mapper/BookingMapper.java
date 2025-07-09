package com.elec_business.booking.mapper;

import com.elec_business.booking.dto.BookingRequestDto;
import com.elec_business.booking.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
    public interface BookingMapper {
        public abstract Booking toEntity(BookingRequestDto dto);
    }


