package com.elec_business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "charging_location", schema = "public", indexes = {
        @Index(name = "idx_charging_location_geom", columnList = "geom")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_station_location", columnNames = {"station_id"})
})
public class ChargingLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Size(max = 255)
    @NotNull
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Size(max = 10)
    @NotNull
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lng", precision = 9, scale = 6)
    private BigDecimal lng;

/*
 TODO [Reverse Engineering] create field to map the 'geom' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "geom", columnDefinition = "geometry")
    private Object geom;
*/
}