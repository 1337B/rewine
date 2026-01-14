package com.rewine.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Wine Route entity.
 */
@Entity
@Table(name = "wine_routes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineRouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String region;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "total_distance")
    private Double totalDistance;

    private String difficulty;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private String status = "draft";

    @OneToMany(mappedBy = "wineRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WineRouteStopEntity> stops = new ArrayList<>();

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
}

