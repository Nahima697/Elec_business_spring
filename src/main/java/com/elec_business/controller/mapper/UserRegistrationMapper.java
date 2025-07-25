package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.RegistrationDto;
import com.elec_business.entity.AppUser;
import org.mapstruct.Mapper;


// Transforme un Dto (input) en entité
@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {
    AppUser toEntity(RegistrationDto dto);
}
