package com.elec_business.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String id;
    private String email;
    private String username;
    private List<UserRoleDTO> roles;
}
