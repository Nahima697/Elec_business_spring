package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserRegisterDto userRegisterDto);
    UserRegisterDto toDto(User appUser);
    UserDTO toDTO(User appUser);
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toRegistrationResponseDto(UserDTO user,String message);
    User toEntity(RegistrationDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget User appUser);
}