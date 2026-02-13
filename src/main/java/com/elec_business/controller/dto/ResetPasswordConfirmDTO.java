package com.elec_business.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmDTO {
    String userId;
    String token;
    String newPassword;
}
