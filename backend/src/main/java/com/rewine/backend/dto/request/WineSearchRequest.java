package com.rewine.backend.dto.request;

import com.rewine.backend.model.enums.WineType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Request parameters for wine search.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WineSearchRequest {

    /**
     * Free text search (matches name, winery name, description).
     */
    @Size(max = 200, message = "Search query must not exceed 200 characters")
    private String search;

    /**
     * Filter by wine type.
     */
    private WineType wineType;

    /**
     * Filter by country.
     */
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    /**
     * Filter by region.
     */
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    /**
     * Filter by vintage year.
     */
    @Min(value = 1900, message = "Vintage must be at least 1900")
    @Max(value = 2100, message = "Vintage must not exceed 2100")
    private Integer vintage;

    /**
     * Minimum price filter.
     */
    @Min(value = 0, message = "Minimum price must be non-negative")
    private BigDecimal minPrice;

    /**
     * Maximum price filter.
     */
    @Min(value = 0, message = "Maximum price must be non-negative")
    private BigDecimal maxPrice;

    /**
     * Minimum rating filter.
     */
    @Min(value = 0, message = "Minimum rating must be non-negative")
    @Max(value = 5, message = "Minimum rating must not exceed 5")
    private BigDecimal minRating;

    /**
     * Only featured wines.
     */
    private Boolean featured;

    /**
     * Sort field.
     */
    @Builder.Default
    private SortField sortBy = SortField.NAME;

    /**
     * Sort direction.
     */
    @Builder.Default
    private SortDirection sortDirection = SortDirection.ASC;

    /**
     * Available sort fields.
     */
    public enum SortField {
        NAME("name"),
        VINTAGE("vintage"),
        PRICE("priceMin"),
        RATING("ratingAverage"),
        CREATED_AT("createdAt");

        private final String fieldName;

        SortField(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }

    /**
     * Sort direction.
     */
    public enum SortDirection {
        ASC,
        DESC
    }

    /**
     * Checks if any filter is active.
     *
     * @return true if any filter is set
     */
    public boolean hasFilters() {
        return Objects.nonNull(search)
                || Objects.nonNull(wineType)
                || Objects.nonNull(country)
                || Objects.nonNull(region)
                || Objects.nonNull(vintage)
                || Objects.nonNull(minPrice)
                || Objects.nonNull(maxPrice)
                || Objects.nonNull(minRating)
                || Boolean.TRUE.equals(featured);
    }
}

