package com.elec_business.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String profilePictureUrl;
    private String email;
    private String username;
}

