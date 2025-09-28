package com.elec_business.controller.dto;


import com.elec_business.entity.User;

public record RegistrationResponseDto(
        User user,
        boolean emailVerificationRequired,
        String message
) {}