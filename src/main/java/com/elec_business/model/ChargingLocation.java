package com.elec_business.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "charging_location", schema = "public")
public class ChargingLocation {
    private UUID id;

    private String addressLine;

    private String city;

    private String postalCode;

    private String country;

    private AppUser user;

    private Set<ChargingStation> chargingStations = new LinkedHashSet<>();

    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "location")
    public Set<ChargingStation> getChargingStations() {
        return chargingStations;
    }

    @Id
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    public UUID getId() {
        return id;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "address_line", nullable = false)
    public String getAddressLine() {
        return addressLine;
    }

    @Size(max = 100)
    @NotNull
    @Column(name = "city", nullable = false, length = 100)
    public String getCity() {
        return city;
    }

    @Size(max = 10)
    @NotNull
    @Column(name = "postal_code", nullable = false, length = 10)
    public String getPostalCode() {
        return postalCode;
    }

    @Size(max = 100)
    @NotNull
    @Column(name = "country", nullable = false, length = 100)
    public String getCountry() {
        return country;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    public AppUser getUser() {
        return user;
    }

}