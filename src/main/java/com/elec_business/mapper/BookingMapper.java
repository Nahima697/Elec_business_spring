package com.elec_business.mapper;

import com.elec_business.dto.BookingRequestDto;
import com.elec_business.model.Booking;
import com.elec_business.model.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {

    @Autowired
    protected ChargingStationRepository chargingStationRepository;

    @Mapping(source = "stationId", target = "station", qualifiedByName = "mapStation")
    public abstract Booking toEntity(BookingRequestDto dto);

    @Named("mapStation")
    protected ChargingStation mapStation(UUID stationId) {
        return chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException(STR."Station not found: \{stationId}"));
    }
}
