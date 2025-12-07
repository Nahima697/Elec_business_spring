package com.elec_business.controller;

import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.business.impl.AvailabilityRuleBusinessImpl;
import com.elec_business.controller.mapper.AvailabilityRuleMapper;
import com.elec_business.entity.AvailabilityRule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/availability_rules")
public class AvailabilityRuleController {

    private final AvailabilityRuleBusinessImpl service;
    private final AvailabilityRuleMapper mapper;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody AvailabilityRuleDto dto) {
        service.createRule(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{stationId}")
    public List<AvailabilityRule> getByStation(@PathVariable String stationId) {
        return service.getRules(stationId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
