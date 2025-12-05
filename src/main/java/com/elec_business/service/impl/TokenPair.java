package com.elec_business.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class TokenPair {

    private String refreshToken;
    private String jwt;
}
