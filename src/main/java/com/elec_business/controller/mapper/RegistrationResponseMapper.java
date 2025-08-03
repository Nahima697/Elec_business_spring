package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.RegistrationResponseDto;
import com.elec_business.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//transforme une entité en Dto (output)
@Mapper(componentModel = "spring")
public interface RegistrationResponseMapper {
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toDto(User user);
}
