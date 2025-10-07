package com.elec_business.controller.dto;

import com.elec_business.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    @NotBlank(message = "username obligatoire")
    private String username;
    @NotBlank(message = "email obligatoire")
    private String email;
    private Boolean emailVerified;
    private String phoneNumber;
    @NotNull(message = "role_id ne peut pas être null")
    private Integer roleId;
}


