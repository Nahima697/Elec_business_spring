package com.elec_business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginCredentialsDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
