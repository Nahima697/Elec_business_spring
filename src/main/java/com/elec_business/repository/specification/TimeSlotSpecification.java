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
                cb.isTrue(root.get("isAvailable"));
    }

    public static Specification<TimeSlot> startAfter(LocalDateTime start) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startTime"), start);
    }

    public static Specification<TimeSlot> endBefore(LocalDateTime end) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("endTime"), end);
    }

    public static Specification<TimeSlot> forDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay   = date.atTime(23, 59, 59);

        return Specification.where(startAfter(startOfDay)).and(endBefore(endOfDay));
    }
}
