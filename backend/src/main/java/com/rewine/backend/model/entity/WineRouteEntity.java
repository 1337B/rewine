package com.rewine.backend.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Wine Route entity representing a curated wine tour route.
 */
@Entity
@Table(name = "wine_routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"stops", "wineries", "createdBy"})
@ToString(exclude = {"stops", "wineries", "createdBy"})
public class WineRouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Country where the route is located.
     */
    private String country;

    /**
     * Region within the country.
     */
    private String region;

    /**
     * Subregion for more granular location.
     */
    private String subregion;

    /**
     * Estimated duration in minutes.
     */
    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    /**
     * Estimated number of days to complete the route.
     */
    @Column(name = "estimated_days")
    private Integer estimatedDays;

    /**
     * Total distance in kilometers.
     */
    @Column(name = "total_distance")
    private Double totalDistance;

    /**
     * Difficulty level: easy, moderate, challenging.
     */
    private String difficulty;

    /**
     * URL of the route's cover image.
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Status: draft, active, archived.
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "draft";

    /**
     * JSON array of recommended wine types.
     * Example: ["RED", "WHITE", "SPARKLING"]
     */
    @Column(name = "recommended_wine_types_json", columnDefinition = "TEXT")
    private String recommendedWineTypesJson;

    /**
     * Ordered stops on this route.
     */
    @OneToMany(mappedBy = "wineRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    @Builder.Default
    private List<WineRouteStopEntity> stops = new ArrayList<>();

    /**
     * Wineries included in this route.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "route_wineries",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "winery_id")
    )
    @Builder.Default
    private Set<WineryEntity> wineries = new HashSet<>();

    /**
     * User who created this route.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Adds a winery to this route.
     *
     * @param winery the winery to add
     */
    public void addWinery(WineryEntity winery) {
        wineries.add(winery);
    }

    /**
     * Removes a winery from this route.
     *
     * @param winery the winery to remove
     */
    public void removeWinery(WineryEntity winery) {
        wineries.remove(winery);
    }
}

