package com.elec_business.controller.dto;

import com.elec_business.service.UrlBuilder;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
        LocationDTO locationDTO,
        List<ReviewResponseDTO> reviewsDTO) {
    public UrlBuilder getUrls() {
        return new UrlBuilder(imageUrl);
    }
}
