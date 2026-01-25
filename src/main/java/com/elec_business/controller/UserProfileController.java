package com.elec_business.controller;

import com.elec_business.business.UserProfilBusiness;
import com.elec_business.controller.dto.UserProfileDto;
import com.elec_business.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDto> uploadAvatar(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(userProfileBusiness.uploadAvatar(currentUser.getId(), file));
    }
}