package com.elec_business.controller.dto;


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
