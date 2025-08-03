package com.elec_business.business;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TimeSlotBusiness {
    void  addTimeSlot(String stationId, Instant startTime, Instant endTime);
    void generateTimeSlotsFromAvailabilityRules(LocalDate startDate, LocalDate endDate, List<AvailabilityRule> rules);
    void purgeOldTimeSlots();
    Page<TimeSlot> getAvailableSlots(String stationId, Pageable pageable);
    Page<TimeSlot> getAvailableSlotsByPeriod(String stationId, Instant startTime, Instant endTime, Pageable pageable);

}
