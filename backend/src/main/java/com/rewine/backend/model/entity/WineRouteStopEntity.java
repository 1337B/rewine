package com.rewine.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Wine Route Stop entity.
 */
@Entity
@Table(name = "wine_route_stops")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineRouteStopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_route_id", nullable = false)
    private WineRouteEntity wineRoute;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String type;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;
}

