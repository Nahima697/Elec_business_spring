package com.elec_business.business;

import com.elec_business.controller.dto.UserProfileDto;

public interface UserProfilBusiness {
    UserProfileDto getProfile(String userId);
    UserProfileDto updateProfile(String userId, UserProfileDto dto);
}
