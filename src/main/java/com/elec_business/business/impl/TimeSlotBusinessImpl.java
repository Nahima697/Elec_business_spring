package com.elec_business.business.impl;

import com.elec_business.business.TimeSlotBusiness;
import com.elec_business.entity.AvailabilityRule;
import com.elec_business.entity.TimeSlot;
import com.elec_business.repository.TimeSlotRepository;
import com.elec_business.entity.ChargingStation;
import com.elec_business.repository.ChargingStationRepository;
import com.elec_business.repository.specification.TimeSlotSpecification;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotBusinessImpl implements TimeSlotBusiness {

    private final TimeSlotRepository timeSlotRepository;
    private final ChargingStationRepository chargingStationRepository;

    @Transactional
    public void addTimeSlot(String stationId, LocalDateTime startTime, LocalDateTime endTime) {
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("Station introuvable"));

        boolean exists = timeSlotRepository.existsSlotInRange(stationId, startTime, endTime, "[]");
        if (exists) {
            throw new IllegalArgumentException("Un créneau existe déjà sur cette période.");
        }

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStation(station);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);
        timeSlot.setIsAvailable(true);
        timeSlot.setAvailability(Range.closedOpen(startTime, endTime));

        timeSlotRepository.save(timeSlot);
    }

    @Transactional
    public void setTimeSlotAvailability(String stationId, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeSlot> slots = timeSlotRepository.findSlotsInRange(stationId, startTime, endTime, "[]");
        for (TimeSlot slot : slots) {
            slot.setIsAvailable(false);
        }
        if (!slots.isEmpty()) {
            timeSlotRepository.saveAll(slots);
        }
    }

    @Transactional
    public void generateTimeSlotsFromAvailabilityRules(LocalDate startDate,
                                                       LocalDate endDate,
                                                       List<AvailabilityRule> rules) {

        log.info("Génération des slots du {} au {} pour {} règles", startDate, endDate, rules.size());
        List<TimeSlot> generatedSlots = new ArrayList<>();

        for (AvailabilityRule rule : rules) {

            if (rule.getDayOfWeek() == null || rule.getDayOfWeek() < 1 || rule.getDayOfWeek() > 7) {
                log.warn("Règle ignorée car jour invalide : {}", rule.getDayOfWeek());
                continue;
            }

            DayOfWeek target = DayOfWeek.of(rule.getDayOfWeek());

            LocalDate current = startDate.with(TemporalAdjusters.nextOrSame(target));

            while (!current.isAfter(endDate)) {

                LocalDateTime start = current.atTime(rule.getStartTime());
                LocalDateTime end = current.atTime(rule.getEndTime());

                if (end.isBefore(start) || end.isEqual(start)) {
                    end = end.plusDays(1);
                }

                // Vérification doublon en base
                boolean exists = timeSlotRepository.existsSlotInRange(
                        rule.getChargingStation().getId(),
                        start,
                        end,
                        "[]"
                );

                if (!exists) {
                    TimeSlot slot = new TimeSlot();
                    slot.setStation(rule.getChargingStation());
                    slot.setStartTime(start);
                    slot.setEndTime(end);
                    slot.setIsAvailable(true);

                    slot.setAvailability(Range.closedOpen(start, end));

                    generatedSlots.add(slot);
                } else {
                    log.debug("Conflit détecté pour la station {} le {}, slot ignoré.", rule.getChargingStation().getId(), start);
                }

                // On passe à la semaine suivante
                current = current.plusWeeks(1);
            }
        }

        if (!generatedSlots.isEmpty()) {
            timeSlotRepository.saveAll(generatedSlots);
            log.info("{} créneaux générés avec succès.", generatedSlots.size());
        } else {
            log.info("Aucun créneau généré (peut-être existent-ils déjà ?)");
        }
    }

    public void purgeOldTimeSlots() {
        timeSlotRepository.deleteByStartTimeBefore(LocalDateTime.now());
    }

    public Page<TimeSlot> getAvailableSlots(String stationId, Pageable pageable) {
        return timeSlotRepository.findByStationId(stationId, pageable);
    }

    public Page<TimeSlot> getAvailableSlotsByPeriod(String stationId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return timeSlotRepository.findAvailableSlotsPage(stationId, startTime, endTime, pageable);
    }

    public List<TimeSlot> getSlotsFiltered(String stationId, LocalDate date) {
        Specification<TimeSlot> spec = Specification.where(null);
        if (stationId != null) spec = spec.and(TimeSlotSpecification.hasStationId(stationId));
        if (date != null) spec = spec.and(TimeSlotSpecification.forDay(date));
        spec = spec.and(TimeSlotSpecification.isAvailable());
        return timeSlotRepository.findAll(spec);
    }
}