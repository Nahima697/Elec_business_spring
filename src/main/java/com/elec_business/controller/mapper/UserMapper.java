package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.*;
import com.elec_business.entity.BillingAddress;
import com.elec_business.entity.User;
import com.elec_business.entity.UserRole;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {

    /* REGISTER → ENTITY */
    @Mapping(target = "roles",
            expression = "java(java.util.Set.of(mapRoleId(userRegisterDto.getRoleId())))")
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

    /* ENTITY → PROFILE DTO */
    @Mapping(source = "roles", target = "roles", qualifiedByName = "toUserRoleDTOList")
    UserProfileDto toUserProfileDto(User user);

    @Mapping(source = "user.id", target = "id")
    @Mapping(target = "addressLine", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "postalCode", ignore = true)
    @Mapping(target = "country", ignore = true)
    UserProfileDto toUserProfileWithDetailDto(User user, BillingAddress address);

    @AfterMapping
    default void enrichWithAddress(
            BillingAddress address,
            @MappingTarget UserProfileDto dto
    ) {
        if (address != null) {
            dto.setAddressLine(address.getAddressLine());
            dto.setCity(address.getCity());
            dto.setPostalCode(address.getPostalCode());
            dto.setCountry(address.getCountry());
        }
    }

    /* Registration */
    @Mapping(target = "roles", ignore = true)
    User toEntity(RegistrationDto dto);

    /* ENTITY → UserDTO (login) */
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
    default List<UserRoleDTO> toUserRoleDTOList(Set<UserRole> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(this::toUserRoleDTO)
                .toList();
    }

    /* PARTIAL UPDATE */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true)
    User partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget User appUser);

    /* Registration Response */
    @Mapping(target = "emailVerificationRequired", expression = "java(true)")
    RegistrationResponseDto toRegistrationResponseDto(UserDTO user, String message);
}
