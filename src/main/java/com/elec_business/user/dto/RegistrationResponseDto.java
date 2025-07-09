package com.elec_business.user.dto;


public record RegistrationResponseDto(
        String username,
        String email,
        boolean emailVerificationRequired
) {}