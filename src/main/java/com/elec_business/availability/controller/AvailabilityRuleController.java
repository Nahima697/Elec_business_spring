package com.elec_business.availability.controller;

import com.elec_business.availability.dto.AvailabilityRuleDto;
import com.elec_business.availability.service.AvailabilityRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/availability-rules")
public class AvailabilityRuleController {

    private final AvailabilityRuleService service;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody AvailabilityRuleDto dto) {
        service.createRule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{stationId}")
    public List<AvailabilityRuleDto> getByStation(@PathVariable UUID stationId) {
        return service.getRules(stationId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
