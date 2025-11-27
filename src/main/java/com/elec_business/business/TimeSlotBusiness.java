package com.elec_business.business;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TimeSlotBusiness {
    void  addTimeSlot(String stationId, LocalDateTime startTime, LocalDateTime endTime);
    void generateTimeSlotsFromAvailabilityRules(LocalDate startDate, LocalDate endDate, List<AvailabilityRule> rules);
    void setTimeSlotAvailability(String stationId, LocalDateTime startTime, LocalDateTime endTime);
    void purgeOldTimeSlots();
    Page<TimeSlot> getAvailableSlots(String stationId, Pageable pageable);
    Page<TimeSlot> getAvailableSlotsByPeriod(String stationId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    List<TimeSlot> getSlotsFiltered(String stationId, LocalDate date);
}
