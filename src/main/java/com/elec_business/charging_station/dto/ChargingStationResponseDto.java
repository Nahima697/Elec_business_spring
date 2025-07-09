package com.elec_business.charging_station.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ChargingStationResponseDto(
        @NotNull UUID id,
        String name,
        String description,
        BigDecimal powerKw,
        BigDecimal price,
        Instant createdAt,
        BigDecimal lng,
        BigDecimal lat,
        String imageUrl) {
}
