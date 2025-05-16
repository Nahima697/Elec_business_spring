package com.elec_business.dto;


public record RegistrationResponseDto(
        String username,
        String email,
        boolean emailVerificationRequired
) {}