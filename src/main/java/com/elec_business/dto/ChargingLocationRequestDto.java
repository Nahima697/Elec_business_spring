package com.elec_business.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class ChargingLocationRequestDto {

    @NotNull(message = "StationId ne peut pas être null")
    private UUID stationId;

    @Size(max = 255)
    @NotNull
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Size(max = 10)
    @NotNull
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "country", nullable = false, length = 100)
    private String country;
}
