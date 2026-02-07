package com.elec_business.controller.mapper;

import com.elec_business.controller.dto.ReviewResponseDTO;
import com.elec_business.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "user.id",     target = "userId")
    @Mapping(source = "station.id",  target = "stationId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "dateToString")
    ReviewResponseDTO toDto(Review review);
     default List<ReviewResponseDTO> toListDto(List<Review> reviews) {
         return reviews.stream().map(this::toDto).toList();
     };

    @Named("dateToString")
    default String dateToString(java.time.OffsetDateTime date) {
        return date != null ? date.toString() : null;
    }
    Review toEntity5(ReviewResponseDTO reviewResponseDTO);

}
