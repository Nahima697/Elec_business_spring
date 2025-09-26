package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.RegistrationDto;
import com.elec_business.controller.dto.UserDTO;
import com.elec_business.controller.dto.UserRegisterDto;
import com.elec_business.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserRegisterDto userRegisterDto);
    UserRegisterDto toDto(User appUser);
    UserDTO toDTO(User appUser);
    User toEntity(RegistrationDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget User appUser);
}