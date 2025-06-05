package com.elec_business.mapper;

import com.elec_business.dto.RegistrationDto;
import com.elec_business.model.AppUser;
import org.mapstruct.Mapper;


// Transforme un Dto (input) en entité
@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {
    AppUser toEntity(RegistrationDto dto);
}
