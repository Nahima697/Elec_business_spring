package com.elec_business.controller;

import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.controller.dto.TimeSlotRequestDto;
import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.controller.mapper.TimeSlotMapper;
import com.elec_business.entity.TimeSlot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/time_slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotBusiness timeSlotBusiness;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public TimeSlotResponseDto addTimeSlot(
            @RequestBody TimeSlotRequestDto requestDto) {

        return timeSlotBusiness.addTimeSlot(
                requestDto.getStationId(),
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );
    }

    @GetMapping("/station/{station_id}")
    public Page<TimeSlotResponseDto> getTimeSlotsForStation(
            @PathVariable("station_id") String stationId,
            @org.springframework.data.web.PageableDefault(size = 10)
            Pageable pageable) {

        return timeSlotBusiness.getAvailableSlots(stationId, pageable);
    }

    @GetMapping("/station/{station_id}/filtered")
    public Page<TimeSlotResponseDto> getFilteredTimeSlots(
            @PathVariable("station_id") String stationId,
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end,
            Pageable pageable) {

        return timeSlotBusiness.getAvailableSlotsByPeriod(
                stationId, start, end, pageable);
    }

    @GetMapping("/station/{station_id}/day")
    public List<TimeSlotResponseDto> getSlotsForDay(
            @PathVariable("station_id") String stationId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return timeSlotBusiness.getSlotsFiltered(stationId, date);
    }
}
