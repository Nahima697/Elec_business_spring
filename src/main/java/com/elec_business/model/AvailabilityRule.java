package com.elec_business.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "availability_rule", schema = "public")
public class AvailabilityRule {
    private UUID id;

    private ChargingStation chargingStation;

    private Integer dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private Instant createdAt;

    private Instant updatedAt;

    @Id
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    public UUID getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "charging_station_id", nullable = false)
    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    @NotNull
    @Column(name = "day_of_week", nullable = false)
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    @NotNull
    @Column(name = "start_time", nullable = false)
    public LocalTime getStartTime() {
        return startTime;
    }

    @NotNull
    @Column(name = "end_time", nullable = false)
    public LocalTime getEndTime() {
        return endTime;
    }

    @ColumnDefault("now()")
    @Column(name = "created_at")
    public Instant getCreatedAt() {
        return createdAt;
    }

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    public Instant getUpdatedAt() {
        return updatedAt;
    }

}