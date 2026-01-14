package com.rewine.backend.model.entity;

import com.rewine.backend.model.enums.WineType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Wine entity representing a wine product.
 */
@Entity
@Table(name = "wines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"winery", "createdByUser"})
@ToString(exclude = {"winery", "createdByUser"})
public class WineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winery_id")
    private WineryEntity winery;

    @Column(nullable = false)
    private String name;

    private Integer vintage;

    @Enumerated(EnumType.STRING)
    @Column(name = "wine_type", nullable = false)
    private WineType wineType;

    private String style;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private List<String> grapes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "TEXT")
    private List<String> allergens;

    @Column(name = "description_es", columnDefinition = "TEXT")
    private String descriptionEs;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "alcohol_content", precision = 4, scale = 2)
    private BigDecimal alcoholContent;

    @Column(name = "serving_temp_min")
    private Integer servingTempMin;

    @Column(name = "serving_temp_max")
    private Integer servingTempMax;

    @Column(name = "price_min", precision = 10, scale = 2)
    private BigDecimal priceMin;

    @Column(name = "price_max", precision = 10, scale = 2)
    private BigDecimal priceMax;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rating_average", precision = 3, scale = 2)
    private BigDecimal ratingAverage;

    @Column(name = "rating_count")
    @Builder.Default
    private Integer ratingCount = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdByUser;

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
     * Gets the winery name safely.
     *
     * @return winery name or null
     */
    public String getWineryName() {
        return Objects.nonNull(winery) ? winery.getName() : null;
    }

    /**
     * Gets the winery region safely.
     *
     * @return winery region or null
     */
    public String getRegion() {
        return Objects.nonNull(winery) ? winery.getRegion() : null;
    }

    /**
     * Gets the winery country safely.
     *
     * @return winery country or null
     */
    public String getCountry() {
        return Objects.nonNull(winery) ? winery.getCountry() : null;
    }
}

