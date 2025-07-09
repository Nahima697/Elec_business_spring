package com.elec_business.charging_station.dto;


import java.util.UUID;

public record ChargingLocationResponseDto(
        UUID id,
        String addressLine,
        String city,
        String postalCode,
        String country,
        UUID userId
) {
}
