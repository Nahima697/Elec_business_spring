package com.elec_business.controller.dto;

import com.elec_business.entity.ChargingStation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Data
@Getter
@Setter
public class AvailabilityRuleDto {

    @NotNull(message ="La station ne doit pas être vide")
    private ChargingStation chargingStation;

    @NotNull
    private Integer dayOfWeek;

    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private Instant createdAt;
}
