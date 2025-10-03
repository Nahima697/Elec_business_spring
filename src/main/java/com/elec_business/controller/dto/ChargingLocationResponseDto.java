package com.elec_business.controller.dto;



public record ChargingLocationResponseDto(
        String id,
        String addressLine,
        String city,
        String postalCode,
        String country,
        String userId
) {
}
