package com.elec_business.controller.dto;

import jakarta.validation.constraints.*;

public record CreateReviewDTO(

        @NotBlank(message = "Le titre est obligatoire")
        String reviewTitle,

        @NotBlank(message = "Le contenu est obligatoire")
        String reviewContent,

        @NotNull(message = "La note est obligatoire")
        @Min(value = 1, message = "La note doit être au minimum 1")
        @Max(value = 5, message = "La note doit être au maximum 5")
        Integer reviewRating,

        @NotBlank(message = "Station ID obligatoire")
        String stationId
) {}
