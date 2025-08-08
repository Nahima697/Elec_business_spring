package com.elec_business.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TimeSlotRequestDto {
    @NotNull(message = "StationId ne peut pas être null")
    private String stationId;

    @NotNull(message = "startDate ne peut pas être null")
    @Future(message = "startDate doit être dans le futur")
    private LocalDateTime startTime;

    @NotNull(message = "endDate ne peut pas être null")
    @Future(message = "endDate doit être dans le futur")
    private LocalDateTime endTime;

}
