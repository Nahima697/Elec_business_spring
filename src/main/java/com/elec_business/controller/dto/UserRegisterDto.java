package com.elec_business.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    private UUID id;
    private String username;
    private String email;
    private Boolean emailVerified;
    private String phoneNumber;
}


