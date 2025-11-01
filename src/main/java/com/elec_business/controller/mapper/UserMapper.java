package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Named("mapRoleId")
    default UserRole mapRoleId(Integer roleId) {
        if (roleId == null) return null;
        UserRole role = new UserRole();
        role.setId(roleId);
        return role;
    }

    @Mapping(source = "roleId", target = "role", qualifiedByName = "mapRoleId")
    User toEntity(UserRegisterDto userRegisterDto);

    UserRegisterDto toDto(User appUser);

    UserDTO toUserDto(User appUser);

    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toRegistrationResponseDto(UserDTO user,String message);
    User toEntity(RegistrationDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget User appUser);
}