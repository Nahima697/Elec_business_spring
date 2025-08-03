package com.elec_business.entity;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;
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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "is_available", nullable = false)
    @ColumnDefault("true")
    private Boolean isAvailable = true;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "availability", columnDefinition = "tsrange", insertable = false, updatable = false)
    private Range<Instant> availability;
}
