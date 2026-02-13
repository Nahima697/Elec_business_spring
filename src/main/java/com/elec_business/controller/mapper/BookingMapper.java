package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.BookingRequestDto;
import com.elec_business.controller.dto.BookingResponseDto;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingStation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface BookingMapper {

    @Mapping(source = "stationId", target = "station")
    Booking toEntity(BookingRequestDto dto);

    Booking responseToEntity(BookingResponseDto dto);

    default ChargingStation toStation(String stationId) {
        if (stationId == null) return null;
        ChargingStation station = new ChargingStation();
        station.setId(stationId);
        return station;
    }

    @Mapping(target = "statusLabel", ignore = true)
    @Mapping(target = "stationName", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "stationOwnerName", ignore = true)
    BookingResponseDto toResponseDto(Booking booking);

    List<BookingResponseDto> toDtos(List<Booking> bookings);

    @AfterMapping
    default void fillExtraInfos(Booking booking, @MappingTarget BookingResponseDto.BookingResponseDtoBuilder builder) {

        if (booking.getStation() != null) {
            builder.stationId(booking.getStation().getId());
            builder.stationName(booking.getStation().getName());
            if (booking.getStation().getLocation() != null &&
                    booking.getStation().getLocation().getUser() != null) {
                builder.stationOwnerName(booking.getStation().getLocation().getUser().getUsername());
            }
        }

        if (booking.getUser() != null) {
            builder.userName(booking.getUser().getUsername());
        }

        if (booking.getStatus() != null) {
            builder.statusLabel(booking.getStatus().getName().name());
        }
    }
}

