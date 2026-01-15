package com.rewine.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing an AI-generated wine comparison.
 * Comparisons are cached per wine pair and language combination.
 * Wine pair is normalized: wine_a_id is always less than wine_b_id.
 */
@Entity
@Table(
        name = "wine_comparisons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_wine_comparisons_wine_pair_language",
                        columnNames = {"wine_a_id", "wine_b_id", "language"}
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"wineA", "wineB"})
@ToString(exclude = {"wineA", "wineB"})
public class WineComparisonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * First wine in the comparison (normalized: always ID < wine_b_id).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_a_id", nullable = false)
    private WineEntity wineA;

    /**
     * Second wine in the comparison (normalized: always ID > wine_a_id).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_b_id", nullable = false)
    private WineEntity wineB;

    @Column(nullable = false, length = 10)
    private String language;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "comparison_json", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> comparisonJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

