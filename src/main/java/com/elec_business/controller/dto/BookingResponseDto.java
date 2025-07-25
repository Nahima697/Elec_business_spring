package com.elec_business.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class BookingResponseDto {
    private UUID id;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal totalPrice;
    private String statusLabel;
    private String stationName;
    private String userName;
    private String stationOwnerName;
}
