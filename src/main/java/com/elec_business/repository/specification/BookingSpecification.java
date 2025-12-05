package com.elec_business.repository.specification;

import com.elec_business.entity.Booking;
import com.elec_business.entity.BookingStatusType;
import org.springframework.data.jpa.domain.Specification;

public class BookingSpecification {

    public static Specification<Booking> hasStatus(BookingStatusType status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("status").get("name"), status);
    }

    public static Specification<Booking> belongsToOwner(String ownerId) {
        return (root, query, cb) ->
                ownerId == null ? null :
                        cb.equal(root.get("station").get("location").get("user").get("id"), ownerId);
    }

    public static Specification<Booking> forStation(String stationId) {
        return (root, query, cb) ->
                stationId == null ? null :
                        cb.equal(root.get("station").get("id"), stationId);
    }
}
