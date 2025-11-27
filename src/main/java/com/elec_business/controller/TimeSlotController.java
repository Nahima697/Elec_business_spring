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
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/time_slots")
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotBusiness timeSlotBusiness;
    private final TimeSlotMapper timeSlotMapper;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TimeSlotResponseDto> addTimeSlot(@RequestBody TimeSlotRequestDto requestDto) {
        TimeSlot timeSlot = timeSlotMapper.toEntity(requestDto);
        timeSlotBusiness.addTimeSlot(requestDto.getStationId(),requestDto.getStartTime(),requestDto.getEndTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(timeSlotMapper.toDto(timeSlot));
    }

    @GetMapping("/station/{station_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<TimeSlotResponseDto>> getTimeSlotsForStation(
            @PathVariable("station_id") String stationId,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable
    ) {
        Page<TimeSlot> slots = timeSlotBusiness.getAvailableSlots(stationId, pageable);
        return ResponseEntity.ok(timeSlotMapper.toDtoPage(slots));
    }

    @GetMapping("/station/{station_id}/filtered")
    public ResponseEntity<Page<TimeSlotResponseDto>> getFilteredTimeSlots(
            @PathVariable("station_id") String stationId,
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end,
            Pageable pageable
    ) {
        Page<TimeSlotResponseDto> slots =timeSlotMapper.toDtoPage(timeSlotBusiness.getAvailableSlotsByPeriod(stationId, start, end, pageable));
        return ResponseEntity.ok(slots);
    }
    @GetMapping("/station/{station_id}/day")
    public ResponseEntity<List<TimeSlotResponseDto>> getSlotsForDay(
            @PathVariable("station_id") String stationId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TimeSlot> slots = timeSlotBusiness.getSlotsFiltered(stationId, date);
        return ResponseEntity.ok(timeSlotMapper.toDtoList(slots));
    }

}


