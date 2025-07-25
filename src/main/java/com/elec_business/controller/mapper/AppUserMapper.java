package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.UserRegisterDto;
import com.elec_business.entity.AppUser;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppUserMapper {
    AppUser toEntity(UserRegisterDto userRegisterDto);
    UserRegisterDto toDto(AppUser appUser);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AppUser partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget AppUser appUser);
}