package com.elec_business.mapper;

import com.elec_business.dto.BookingRequestDto;
import com.elec_business.dto.ChargingLocationRequestDto;
import com.elec_business.entity.Booking;
import com.elec_business.entity.ChargingLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChargingLocationMapper {
    ChargingLocationMapper INSTANCE = Mappers.getMapper(ChargingLocationMapper.class);
    @Mapping(source = "stationId", target = "station", qualifiedByName = "mapStation")
    public abstract ChargingLocation toEntity(ChargingLocationRequestDto dto);
}
