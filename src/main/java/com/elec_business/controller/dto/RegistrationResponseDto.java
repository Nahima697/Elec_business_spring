package com.elec_business.controller.dto;


public record RegistrationResponseDto(
        UserDTO user,
        boolean emailVerificationRequired,
        String message
) {}