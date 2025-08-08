package com.elec_business.controller.dto;

import java.time.LocalDateTime;

public record TimeSlotResponseDto(String id, String stationId, String stationName, LocalDateTime startTime, LocalDateTime endTime) {
}
