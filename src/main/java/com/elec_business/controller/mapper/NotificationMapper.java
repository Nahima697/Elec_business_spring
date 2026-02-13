package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.NotificationResponseDTO;
import com.elec_business.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    List<NotificationResponseDTO> toDTO(List<Notification> notifications);
}
