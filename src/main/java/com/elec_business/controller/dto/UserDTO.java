package com.elec_business.controller.dto;

import com.elec_business.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDTO {
    private String id;
    private String email;
    private UserRole role;
}
