package com.elec_business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.swing.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "time_slot", schema = "public", indexes = {
        @Index(name = "idx_timeslot_range", columnList = "availability")
})
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    @Column(name = "availability", insertable = false, updatable = false)
    private String availability;

    private Boolean isAvailable = false;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_available", nullable = false)
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    @NotNull
    @Column(name = "end_time", nullable = false)
    public OffsetDateTime getEndTime() {
        return endTime;
    }

    @NotNull
    @Column(name = "start_time", nullable = false)
    public OffsetDateTime getStartTime() {
        return startTime;
    }

}