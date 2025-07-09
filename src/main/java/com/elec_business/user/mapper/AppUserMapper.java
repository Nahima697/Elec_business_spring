package com.elec_business.user.mapper;

import com.elec_business.user.dto.UserRegisterDto;
import com.elec_business.user.model.AppUser;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppUserMapper {
    AppUser toEntity(UserRegisterDto userRegisterDto);
    UserRegisterDto toDto(AppUser appUser);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AppUser partialUpdate(UserRegisterDto userRegisterDto, @MappingTarget AppUser appUser);
}