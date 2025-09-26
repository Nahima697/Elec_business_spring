package com.elec_business.controller.dto;


public record RegistrationResponseDto(
        String username,
        String email,
        boolean emailVerificationRequired,
        String message
) {}