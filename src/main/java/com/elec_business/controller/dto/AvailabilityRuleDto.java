package com.elec_business.controller.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class AvailabilityRuleDto {

    @NotNull(message = "L'ID de la station est obligatoire")
    private String stationId;

    @NotNull
    private Integer dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

}