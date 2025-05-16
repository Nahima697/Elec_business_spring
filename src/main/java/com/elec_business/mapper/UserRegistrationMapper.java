package com.elec_business.mapper;

import com.elec_business.dto.RegistrationDto;
import com.elec_business.entity.AppUser;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {
    AppUser toEntity(RegistrationDto dto);
    RegistrationDto toDto(AppUser user);
}
