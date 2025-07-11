package com.elec_business.availability.service;

import com.elec_business.availability.mapper.AvailabilityRuleMapper;
import com.elec_business.availability.dto.AvailabilityRuleDto;
import com.elec_business.availability.model.AvailabilityRule;
import com.elec_business.availability.repository.AvailabilityRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityRuleService {

    private final AvailabilityRuleRepository ruleRepo;
    private final AvailabilityRuleMapper mapper;
    private final TimeSlotService timeSlotService;

    public void createRule(AvailabilityRuleDto dto) {
        AvailabilityRule rule = mapper.toEntity(dto);
        ruleRepo.save(rule);

        // générer les slots automatiquement

        timeSlotService.generateTimeSlotsFromAvailabilityRules(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                List.of(rule)
        );
    }

    public List<AvailabilityRuleDto> getRules(UUID stationId) {
        return ruleRepo.findByChargingStation_Id(stationId)
                .stream().map(mapper::toDto).toList();
    }

    public void deleteRule(UUID id) {
        ruleRepo.deleteById(id);
    }
}
