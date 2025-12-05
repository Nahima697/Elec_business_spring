package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ReviewResponseDTO;
import com.elec_business.entity.Review;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "user.id",     target = "userId")
    @Mapping(source = "station.id",  target = "stationId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "dateToString")
    ReviewResponseDTO toDto(Review review);

    @Named("dateToString")
    default String dateToString(java.time.OffsetDateTime date) {
        return date != null ? date.toString() : null;
    }
}
