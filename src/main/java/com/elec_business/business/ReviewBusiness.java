package com.elec_business.business;

import com.elec_business.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewBusiness {
   Double getAverageRating(String stationId);
   Long getReviewCount(String stationId);
   Page<Review> getReviews(String stationId, Pageable pageable);
    Review createReview(String reviewTitle, String reviewContent,Integer reviewRating,String userId,String stationId);
}
