package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {

    /* ----------------------------
          REGISTER → ENTITY
       ---------------------------- */
    @Mapping(source = "roleId", target = "roles",
            expression = "java(java.util.List.of(mapRoleId(userRegisterDto.getRoleId())))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "refreshTokens", ignore = true)
    User toEntity(UserRegisterDto userRegisterDto);

    @Named("mapRoleId")
    default UserRole mapRoleId(Integer id) {
        if (id == null) return null;
        UserRole role = new UserRole();
        role.setId(id);
        return role;
    }

    @Mapping(source = "roles", target = "roles", qualifiedByName = "toUserRoleDTOList")
    UserProfileDto toUserProfileDto(User user);

    @Mapping(target = "roles", ignore = true) // Car le registration ne donne pas encore de rôle
    User toEntity(RegistrationDto dto);

    /* ----------------------------
            ENTITY → DTO
       ---------------------------- */
    @Mapping(source = "roles", target = "roles", qualifiedByName = "toUserRoleDTOList")
    UserDTO toUserDto(User user);

    @Named("toUserRoleDTO")
    default UserRoleDTO toUserRoleDTO(UserRole role) {
        if (role == null) return null;

        UserRoleDTO dto = new UserRoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }

    @Named("toUserRoleDTOList")
    default java.util.List<UserRoleDTO> toUserRoleDTOList(java.util.List<UserRole> roles) {
        if (roles == null) return java.util.List.of();

        return roles.stream()
                .map(this::toUserRoleDTO)
                .toList();
    }

    /* ----------------------------
        PARTIAL UPDATE
       ---------------------------- */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true)
    User partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget User appUser);

    /* ----------------------------
      REGISTRATION RESPONSE
       ---------------------------- */
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toRegistrationResponseDto(UserDTO user, String message);
}
