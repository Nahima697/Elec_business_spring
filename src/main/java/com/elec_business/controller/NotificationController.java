package com.elec_business.controller;

import com.elec_business.business.NotificationBusiness;
import com.elec_business.entity.Notification;
import com.elec_business.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationBusiness notificationBusiness;

    @GetMapping
    public List<Notification> getMyNotifications(@AuthenticationPrincipal User user) {
        return notificationBusiness.getMyNotifications(user);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        notificationBusiness.markAsRead(id);
    }
}