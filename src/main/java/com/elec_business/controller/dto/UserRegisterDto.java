package com.elec_business.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRegisterDto {

    @NotBlank(message = "Le username est obligatoire")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private Boolean emailVerified;

    private String phoneNumber;

    @NotNull(message = "roleId ne peut pas être nul")
    private Integer roleId;
}
