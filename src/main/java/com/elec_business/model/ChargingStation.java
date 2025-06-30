package com.elec_business.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "charging_station", schema = "public", indexes = {
        @Index(name = "idx_station_user", columnList = "user_id")
})
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("uuid_generate_v4()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;


    @Column(name = "power_kw", precision = 5, scale = 2)
    private BigDecimal powerKw;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lng", precision = 9, scale = 6)
    private BigDecimal lng;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private ChargingLocation location;

    private String imageUrl;

    @Column(name = "image_url", length = Integer.MAX_VALUE)
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChargingStation that = (ChargingStation) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description)  && Objects.equals(powerKw, that.powerKw) && Objects.equals(price, that.price) && Objects.equals(createdAt, that.createdAt) && Objects.equals(lat, that.lat) && Objects.equals(lng, that.lng) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, powerKw, price, createdAt, lat, lng, location);
    }

    @Override
    public String toString() {
        return "ChargingStation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", powerKw=" + powerKw +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", lat=" + lat +
                ", lng=" + lng +
                ", location=" + location +
                '}';
    }
}