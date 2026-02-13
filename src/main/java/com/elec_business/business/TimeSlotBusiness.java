package com.elec_business.business;

import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.entity.AvailabilityRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotBusiness {

    TimeSlotResponseDto addTimeSlot(
            String stationId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    void generateTimeSlotsFromAvailabilityRules(
            LocalDate startDate,
            LocalDate endDate,
            List<AvailabilityRule> rules
    );

    void setTimeSlotAvailability(
            String stationId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    void purgeOldTimeSlots();

    Page<TimeSlotResponseDto> getAvailableSlots(
            String stationId,
            Pageable pageable
    );

    Page<TimeSlotResponseDto> getAvailableSlotsByPeriod(
            String stationId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );

    List<TimeSlotResponseDto> getSlotsFiltered(
            String stationId,
            LocalDate date
    );
}
