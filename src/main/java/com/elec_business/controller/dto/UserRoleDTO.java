package com.elec_business.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleDTO {

    @NotNull(message = "L'id du rôle ne peut pas être nul")
    private Integer id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    private String name;
}
