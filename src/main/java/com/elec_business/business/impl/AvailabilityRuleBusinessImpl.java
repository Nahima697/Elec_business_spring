package com.elec_business.business.impl;

import com.elec_business.business.AvailabilityRuleBusiness;
import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.business.exception.StationNotFoundException;
import com.elec_business.controller.dto.AvailabilityRuleDto;
import com.elec_business.controller.mapper.AvailabilityRuleMapper;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.ChargingStation;
import com.elec_business.entity.User;
import com.elec_business.repository.AvailabilityRuleRepository;
import com.elec_business.repository.ChargingStationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityRuleBusinessImpl implements AvailabilityRuleBusiness {

    private final AvailabilityRuleRepository ruleRepo;
    private final TimeSlotBusinessImpl timeSlotService;
    private final ChargingStationRepository chargingStationRepository;
    private final AvailabilityRuleMapper ruleMapper;

    @Override
    @Transactional
    public void createRule(AvailabilityRuleDto ruleDto, User currentUser) {

        // 1. Récupération ID station
        AvailabilityRule rule = ruleMapper.toEntity(ruleDto);
        String stationId = rule.getChargingStation().getId();

        // 2. Récupération Station
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);

        // 3. Sécurité (Propriétaire)
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedStationException();
        }

        // 4. Vérification Conflit
        boolean hasConflict = ruleRepo.existsOverlappingRule(
                station.getId(),
                rule.getDayOfWeek(),
                rule.getStartTime(),
                rule.getEndTime()
        );

        if (hasConflict) {
            throw new IllegalArgumentException("Une règle existe déjà sur ce créneau horaire pour ce jour !");
        }

        // 5. Rattachement
        rule.setChargingStation(station);

        AvailabilityRule savedRule = ruleRepo.save(rule);

        // 7. Génération des créneaux (Sur la règle sauvegardée)
        timeSlotService.generateTimeSlotsFromAvailabilityRules(
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                List.of(savedRule)
        );
    }

    @Transactional
    public List<AvailabilityRuleDto> getRules(String stationId) {

        return ruleMapper.toDtos(ruleRepo.findByChargingStation_Id(stationId));
    }

    public void deleteRule(String id) {
        ruleRepo.deleteById(id);
    }
}