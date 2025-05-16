package com.elec_business.mapper;

import com.elec_business.dto.UserProfileDto;
import com.elec_business.entity.AppUser;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppUserMapper {
    AppUser toEntity(UserProfileDto userProfileDto);

    UserProfileDto toDto(AppUser appUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AppUser partialUpdate(UserProfileDto userProfileDto, @MappingTarget AppUser appUser);
}