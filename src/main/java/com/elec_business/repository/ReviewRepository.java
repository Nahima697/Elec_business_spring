package com.elec_business.repository;

import com.elec_business.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    Page<Review> findByStationIdOrderByCreatedAtDesc(String stationId, Pageable pageable);

    boolean existsByUserIdAndStationId(String userId, String stationId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.station.id = :stationId")
    Optional<Double> findAverageRatingByStationId(@Param("stationId") String stationId);

    Long countByStationId(String stationId);

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
        FROM Review r
        WHERE r.user.id = :userId AND r.station.id = :stationId
    """)
    boolean existsByUserAndStation(@Param("userId") String userId, @Param("stationId") String stationId);
}
