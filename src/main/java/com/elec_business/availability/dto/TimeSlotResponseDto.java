package com.elec_business.availability.dto;

import java.time.Instant;
import java.util.UUID;

public record TimeSlotResponseDto(UUID id, UUID stationId, String stationName, Instant startTime, Instant endTime) {
}
