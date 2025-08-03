package com.elec_business.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {

    private String refreshToken;
    private String jwt;
}
