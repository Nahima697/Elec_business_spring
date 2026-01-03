package com.elec_business.business.impl;

import com.elec_business.business.AvailabilityRuleBusiness;
import com.elec_business.business.exception.AccessDeniedStationException;
import com.elec_business.business.exception.StationNotFoundException;
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
    private final AvailabilityRuleMapper mapper;
    private final TimeSlotBusinessImpl timeSlotService;
    private final ChargingStationRepository chargingStationRepository;

    @Override
    public void createRule(AvailabilityRule rule, User currentUser) {

        // 1. On récupère l'ID de la station directement depuis l'objet rule
        String stationId = rule.getChargingStation().getId();

        // 2. On va chercher la station en base de données
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);

        // 3. Vérification de sécurité
        if (!station.getLocation().getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedStationException();
        }
        boolean hasConflict = ruleRepo.existsOverlappingRule(
                station.getId(),
                rule.getDayOfWeek(),
                rule.getStartTime(),
                rule.getEndTime()
        );

        if (hasConflict) {
            throw new IllegalArgumentException("Une règle existe déjà sur ce créneau horaire pour ce jour !");
        }
        // 4. On rattache la station complète à la règle
        rule.setChargingStation(station);

        // 5. Sauvegarde
        ruleRepo.save(rule);

        // 6. Génération des créneaux
        timeSlotService.generateTimeSlotsFromAvailabilityRules(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                List.of(rule)
        );
    }

    @Transactional
    public List<AvailabilityRule> getRules(String stationId) {
        return ruleRepo.findByChargingStation_Id(stationId)
                .stream().toList();
    }

    public void deleteRule(String id) {
        ruleRepo.deleteById(id);
    }
}
