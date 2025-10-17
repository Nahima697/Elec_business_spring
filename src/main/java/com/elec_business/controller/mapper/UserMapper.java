package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "roleId", target = "role.id")
    User toEntity(UserRegisterDto userRegisterDto);
    UserRegisterDto toDto(User appUser);
<<<<<<< HEAD
    UserDTO toUserDto(User appUser);
=======
    UserDTO toDTO(User appUser);
>>>>>>> 1939fc473334638ae29f95a7d0395f966f490996
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toRegistrationResponseDto(UserDTO user,String message);
    User toEntity(RegistrationDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget User appUser);
}