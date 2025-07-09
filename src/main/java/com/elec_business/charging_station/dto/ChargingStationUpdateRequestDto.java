package com.elec_business.charging_station.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;


public record ChargingStationUpdateRequestDto(
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        String description,

        UUID typeId,

        @Positive(message = "Power must be positive")
        BigDecimal powerKw,

        @Positive(message = "Price must be positive")
        BigDecimal price
) {
    public boolean isEmpty() {
        return name == null &&
                description == null &&
                typeId == null &&
                powerKw == null &&
                price == null;
    }
}
