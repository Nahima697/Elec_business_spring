package com.elec_business.controller;

import com.elec_business.business.UserProfilBusiness;
import com.elec_business.controller.dto.UserProfileDto;
import com.elec_business.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfilBusiness userProfileBusiness;

    @GetMapping
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userProfileBusiness.getProfile(currentUser.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(userProfileBusiness.updateProfile(currentUser.getId(), dto));
    }
}