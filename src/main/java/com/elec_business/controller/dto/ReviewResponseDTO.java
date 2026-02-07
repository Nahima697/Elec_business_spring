package com.elec_business.controller.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ReviewResponseDTO {

    private String id;

    private String reviewTitle;

    private String reviewContent;

    private Integer reviewRating;

    private String userId;

    private String stationId;

    private String username;

    OffsetDateTime createdAt;
}
