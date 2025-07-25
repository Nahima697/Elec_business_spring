package com.elec_business.controller;

import com.elec_business.controller.dto.TimeSlotRequestDto;
import com.elec_business.controller.dto.TimeSlotResponseDto;
import com.elec_business.business.impl.TimeSlotBusinessImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/time_slots")
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotBusinessImpl timeSlotService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TimeSlotResponseDto> addTimeSlot(@RequestBody TimeSlotRequestDto requestDto) {
        TimeSlotResponseDto responseDto = timeSlotService.addTimeSlot(
                requestDto.getStationId(),
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/station/{station_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<TimeSlotResponseDto>> getTimeSlotsForStation(
            @PathVariable("station_id") UUID stationId,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable
    ) {
        Page<TimeSlotResponseDto> slots = timeSlotService.getAvailableSlots(stationId, pageable);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/station/{station_id}/filtered")
    public ResponseEntity<Page<TimeSlotResponseDto>> getFilteredTimeSlots(
            @PathVariable("station_id") UUID stationId,
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            Pageable pageable
    ) {
        Page<TimeSlotResponseDto> slots = timeSlotService.getAvailableSlotsByPeriode(stationId, start, end, pageable);
        return ResponseEntity.ok(slots);
    }

}


