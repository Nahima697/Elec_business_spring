package com.elec_business.scheduler;

import com.elec_business.model.AvailabilityRule;
import com.elec_business.repository.AvailabilityRuleRepository;
import com.elec_business.service.AvailabilityRuleService;
import com.elec_business.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor

public class AvailabilityScheduler {
    private final AvailabilityRuleService availabilityRuleService;
    private final TimeSlotService timeSlotService;
    private final AvailabilityRuleRepository availabilityRuleRepository;

    @Scheduled(cron = "0 0 1 * * *") // tous les jours à 1h
    public void generateTimeSlotsNightly() {
        // 🧹 1. Supprime les créneaux passés
        timeSlotService.purgeOldTimeSlots();

        // 📅 2. Récupère toutes les règles
        List<AvailabilityRule> rules = availabilityRuleRepository.findAll();

        // ⚙️ 3. Génère les créneaux pour les 14 prochains jours
        timeSlotService.generateTimeSlotsFromAvailabilityRules(
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                rules
        );
    }
}
