package com.elec_business.user.mapper;

import com.elec_business.user.dto.RegistrationResponseDto;
import com.elec_business.user.model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//transforme une entité en Dto (output)
@Mapper(componentModel = "spring")
public interface RegistrationResponseMapper {
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toDto(AppUser user);
}
