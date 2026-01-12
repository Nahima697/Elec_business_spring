package com.elec_business.business.scheduler;

import com.elec_business.entity.AvailabilityRule;
import com.elec_business.repository.AvailabilityRuleRepository;
import com.elec_business.business.impl.AvailabilityRuleBusinessImpl;
import com.elec_business.business.impl.TimeSlotBusinessImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor

public class AvailabilityScheduler {
    private final AvailabilityRuleBusinessImpl availabilityRuleService;
    private final TimeSlotBusinessImpl timeSlotService;
    private final AvailabilityRuleRepository availabilityRuleRepository;

    @Scheduled(cron = "0 0 1 * * *") // tous les jours à 1h
    public void generateTimeSlotsNightly() {
        //  1. Supprime les créneaux passés
        timeSlotService.purgeOldTimeSlots();

        // 2. Récupère toutes les règles
        List<AvailabilityRule> rules = availabilityRuleRepository.findAll();

        // 3. Génère les créneaux pour les 14 prochains jours
        timeSlotService.generateTimeSlotsFromAvailabilityRules(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                rules
        );
    }
}
