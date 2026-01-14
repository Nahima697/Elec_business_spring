package com.elec_business.repository.specification;

import com.elec_business.entity.TimeSlot;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeSlotSpecification {

    public static Specification<TimeSlot> hasStationId(String stationId) {
        return (root, query, cb) ->
                cb.equal(root.get("station").get("id"), stationId);
    }

    public static Specification<TimeSlot> isAvailable() {
        return (root, query, cb) ->
                cb.isTrue(cb.coalesce(root.get("isAvailable"), false));
    }

    public static Specification<TimeSlot> forDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay(); // 00:00:00 Lendemain
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("startTime"), startOfDay),
                cb.lessThan(root.get("startTime"), startOfNextDay)
        );
    }
}