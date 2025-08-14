package com.elec_business.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookingResponseDto {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
    private BigDecimal totalPrice;
    private String statusLabel;
    private String stationName;
    private String userName;
    private String stationOwnerName;
}
