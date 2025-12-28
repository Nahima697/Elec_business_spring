package com.elec_business.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String id;
    private String profilePictureUrl;
    private String email;
    private String username;
    private List<UserRoleDTO> roles;
    private String addressLine;
    private String city;
    private String postalCode;
    private String country;
}
