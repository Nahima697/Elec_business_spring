package com.elec_business.controller;

import com.elec_business.dto.TimeSlotRequestDto;
import com.elec_business.dto.TimeSlotResponseDto;
import com.elec_business.mapper.TimeSlotResponseMapper;
import com.elec_business.model.AppUser;
import com.elec_business.model.TimeSlot;
import com.elec_business.repository.TimeSlotRepository;
import com.elec_business.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    @PostMapping("/time_slots")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TimeSlotResponseDto> addTimeSlot(@RequestBody TimeSlotRequestDto requestDto) {
        TimeSlotResponseDto responseDto = timeSlotService.addTimeSlot(
                requestDto.getStationId(),
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}


