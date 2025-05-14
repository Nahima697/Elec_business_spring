package com.elec_business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

/*
 TODO [Reverse Engineering] create field to map the 'availability' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "availability", columnDefinition = "tsrange not null")
    private Object availability;
*/
}