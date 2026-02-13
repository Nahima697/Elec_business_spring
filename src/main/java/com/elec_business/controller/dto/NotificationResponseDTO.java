package com.elec_business.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {
    private String id;
    private String type;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}