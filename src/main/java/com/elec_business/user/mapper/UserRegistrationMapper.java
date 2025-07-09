package com.elec_business.user.mapper;

import com.elec_business.user.dto.RegistrationDto;
import com.elec_business.user.model.AppUser;
import org.mapstruct.Mapper;


// Transforme un Dto (input) en entité
@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {
    AppUser toEntity(RegistrationDto dto);
}
