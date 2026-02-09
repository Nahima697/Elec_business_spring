package com.elec_business.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    @NotNull(message = "StationId ne peut pas être null")
    private String stationId;
    @NotNull(message = "startDate ne peut pas être null")
    @Future(message = "startDate doit être dans le futur")
    private LocalDateTime startDate;
    @NotNull(message = "endDate ne peut pas être null")
    @Future(message = "endDate doit être dans le futur")
    private LocalDateTime endDate;

}
