package com.elec_business.mapper;

import com.elec_business.dto.RegistrationResponseDto;
import com.elec_business.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//transforme une entité en Dto (output)
@Mapper(componentModel = "spring")
public interface RegistrationResponseMapper {
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toDto(AppUser user);
}
