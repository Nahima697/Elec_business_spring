package com.elec_business.booking.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingResponseDto (
    UUID id,
    Instant startDate,
    Instant endDate,
    BigDecimal totalPrice,
    String statusLabel,
    String stationName,
    String userName) {
}