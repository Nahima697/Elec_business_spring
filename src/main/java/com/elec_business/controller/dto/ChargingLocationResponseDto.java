package com.elec_business.controller.dto;


import java.util.UUID;

public record ChargingLocationResponseDto(
        String id,
        String addressLine,
        String city,
        String postalCode,
        String country,
        String userId
) {
}
