package com.elec_business.dto;

import java.util.UUID;

public record ChargingLocationResponseDto(
        UUID stationId,
        String addressLine,
        String city,
        String postalCode,
        String country
) {
}
