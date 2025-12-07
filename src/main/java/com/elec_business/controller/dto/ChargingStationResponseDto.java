package com.elec_business.controller.dto;

import com.elec_business.service.UrlBuilder;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record ChargingStationResponseDto(
        @NotNull String id,
        String name,
        String description,
        BigDecimal powerKw,
        BigDecimal price,
        Instant createdAt,
        BigDecimal lng,
        BigDecimal lat,
        String imageUrl,
        LocationDTO locationDTO) {
    public UrlBuilder getUrls() {
        return new UrlBuilder(imageUrl);
    }
}
