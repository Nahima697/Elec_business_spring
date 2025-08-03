package com.elec_business.controller.dto;

import com.elec_business.service.UrlBuilder;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ChargingStationRequestDto {

    @Size(max = 100)
    @NotBlank

    private String name;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal powerKw;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Instant createdAt;
    @NotNull
    private BigDecimal lat;
    @NotNull
    private BigDecimal lng;

    @NotNull(message = "locationId ne peut pas être null")
    private String locationId;

    private MultipartFile image;


}
