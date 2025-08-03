package com.elec_business.business.impl;

import com.elec_business.business.AvailabilityRuleBusiness;
import com.elec_business.controller.mapper.AvailabilityRuleMapper;
import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.repository.AvailabilityRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityRuleBusinessImpl implements AvailabilityRuleBusiness {

    private final AvailabilityRuleRepository ruleRepo;
    private final AvailabilityRuleMapper mapper;
    private final TimeSlotBusinessImpl timeSlotService;

    public void createRule(AvailabilityRule rule) {

        ruleRepo.save(rule);

        // générer les slots automatiquement

        timeSlotService.generateTimeSlotsFromAvailabilityRules(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                List.of(rule)
        );
    }

    public List<AvailabilityRule> getRules(String stationId) {
        return ruleRepo.findByChargingStation_Id(stationId)
                .stream().toList();
    }

    public void deleteRule(String id) {
        ruleRepo.deleteById(id);
    }
}
