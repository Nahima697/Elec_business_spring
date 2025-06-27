package com.elec_business.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "time_slot", schema = "public", indexes = {
        @Index(name = "idx_timeslot_range", columnList = "availability")
}, uniqueConstraints = {
        @UniqueConstraint(name = "no_overlap", columnNames = {"station_id", "availability"})
})
public class TimeSlot {
    private UUID id;

    private ChargingStation station;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;
    private Boolean isAvailable = false;

    @Id
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    public UUID getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "station_id", nullable = false)
    public ChargingStation getStation() {
        return station;
    }

    @NotNull
    @Column(name = "start_time", nullable = false)
    public OffsetDateTime getStartTime() {
        return startTime;
    }

    @NotNull
    @Column(name = "end_time", nullable = false)
    public OffsetDateTime getEndTime() {
        return endTime;
    }

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_available", nullable = false)
    public Boolean getIsAvailable() {
        return isAvailable;
    }

/*
 TODO [Reverse Engineering] create field to map the 'availability' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    private Object availability;
*/
}