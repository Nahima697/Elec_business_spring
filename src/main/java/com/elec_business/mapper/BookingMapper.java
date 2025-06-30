package com.elec_business.mapper;

import com.elec_business.dto.BookingRequestDto;
import com.elec_business.model.Booking;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
    public interface BookingMapper {
        public abstract Booking toEntity(BookingRequestDto dto);
    }


