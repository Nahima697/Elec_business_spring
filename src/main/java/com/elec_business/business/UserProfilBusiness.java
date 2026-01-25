package com.elec_business.business;

import com.elec_business.controller.dto.UserProfileDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfilBusiness {
    UserProfileDto getProfile(String userId);
    UserProfileDto updateProfile(String userId, UserProfileDto dto);
    UserProfileDto uploadAvatar(String userId, MultipartFile file);
}
