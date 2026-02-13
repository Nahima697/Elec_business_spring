package com.elec_business.controller;

import com.elec_business.business.NotificationBusiness;
import com.elec_business.controller.dto.NotificationResponseDTO;
import com.elec_business.controller.mapper.NotificationMapper;
import com.elec_business.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationBusiness notificationBusiness;
    private final NotificationMapper notificationMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(@AuthenticationPrincipal User user) {
        List<NotificationResponseDTO> notifications = notificationMapper.toDTO(notificationBusiness.getMyNotifications(user));
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        notificationBusiness.markAsRead(id);
    }
}