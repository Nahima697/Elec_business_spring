package com.elec_business.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ChargingStationResponseDto(
        UUID id,
        String name,
        String description,
        BigDecimal powerKw,
        BigDecimal price,
        Instant createdAt,
        BigDecimal lng,
        BigDecimal lat,
        String imageUrl) {
}
