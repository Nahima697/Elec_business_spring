package com.elec_business.repository;

import com.elec_business.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository  extends JpaRepository<Notification, UUID> {
}
