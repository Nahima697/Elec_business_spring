package com.elec_business.controller.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;



public record ChargingStationUpdateRequestDto(
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,
        String description,
        @Positive(message = "Power must be positive")
        BigDecimal powerKw,
        @Positive(message = "Price must be positive")
        BigDecimal price
) {
    public boolean isEmpty() {
        return name == null &&
                description == null &&
                powerKw == null &&
                price == null;
    }
}
