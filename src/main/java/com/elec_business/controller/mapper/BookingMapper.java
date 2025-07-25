package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    // Mapping du DTO vers l'entité Booking
    public abstract Booking toEntity(BookingRequestDto dto);

    // Mapping de l'entité Booking vers le DTO
    public abstract BookingResponseDto toDto(Booking booking);

    public abstract List<BookingResponseDto> toDtos(List<Booking> bookings);

    // Méthode appelée après le mapping pour injecter les infos personnalisées
    @AfterMapping
    protected void fillExtraInfos(Booking booking, @MappingTarget BookingResponseDto.BookingResponseDtoBuilder builder) {
        if (booking.getStation() != null) {
            builder.stationName(booking.getStation().getName());

            if (booking.getStation().getLocation() != null &&
                    booking.getStation().getLocation().getUser() != null) {
                builder.stationOwnerName(booking.getStation().getLocation().getUser().getUsername());
                builder.statusLabel(booking.getStatus().getName());
            }
        }

        if (booking.getUser() != null) {
            builder.userName(booking.getUser().getUsername());
        }
    }
}
