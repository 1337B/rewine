package com.rewine.backend.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper with frontend-friendly naming.
 *
 * @param <T> the type of content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    /**
     * The content items for the current page.
     */
    @Schema(description = "Items in the current page")
    private List<T> items;

    /**
     * Current page number (0-indexed).
     */
    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int pageNumber;

    /**
     * Number of items per page.
     */
    @Schema(description = "Number of items per page", example = "20")
    private int pageSize;

    /**
     * Total number of items across all pages.
     */
    @Schema(description = "Total number of items across all pages", example = "150")
    private long totalItems;

    /**
     * Total number of pages.
     */
    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;

    /**
     * Whether this is the first page.
     */
    @Schema(description = "Whether this is the first page")
    private boolean first;

    /**
     * Whether this is the last page.
     */
    @Schema(description = "Whether this is the last page")
    private boolean last;

    /**
     * Whether there are more pages after this one.
     */
    @Schema(description = "Whether there are more pages after this one")
    private boolean hasNext;

    /**
     * Whether there are pages before this one.
     */
    @Schema(description = "Whether there are pages before this one")
    private boolean hasPrevious;

    // ========================================
    // Legacy field accessors for backward compatibility.
    // These will be serialized with the old names alongside the new names.
    // Remove after frontend migration is complete.
    // ========================================

    /**
     * Legacy accessor for items (previously named 'content').
     * @deprecated Use {@link #getItems()} instead
     */
    @JsonProperty("content")
    @Schema(description = "Deprecated: Use 'items' instead", deprecated = true)
    @Deprecated
    public List<T> getContent() {
        return items;
    }

    /**
     * Legacy accessor for pageNumber (previously named 'page').
     * @deprecated Use {@link #getPageNumber()} instead
     */
    @JsonProperty("page")
    @Schema(description = "Deprecated: Use 'pageNumber' instead", deprecated = true)
    @Deprecated
    public int getPage() {
        return pageNumber;
    }

    /**
     * Legacy accessor for pageSize (previously named 'size').
     * @deprecated Use {@link #getPageSize()} instead
     */
    @JsonProperty("size")
    @Schema(description = "Deprecated: Use 'pageSize' instead", deprecated = true)
    @Deprecated
    public int getSize() {
        return pageSize;
    }

    /**
     * Legacy accessor for totalItems (previously named 'totalElements').
     * @deprecated Use {@link #getTotalItems()} instead
     */
    @JsonProperty("totalElements")
    @Schema(description = "Deprecated: Use 'totalItems' instead", deprecated = true)
    @Deprecated
    public long getTotalElements() {
        return totalItems;
    }

    /**
     * Creates a PageResponse from Spring Data's Page object.
     *
     * @param page    the Spring Data page
     * @param items   the mapped content items
     * @param <T>     the content type
     * @param <E>     the entity type
     * @return the page response
     */
    public static <T, E> PageResponse<T> of(org.springframework.data.domain.Page<E> page, List<T> items) {
        return PageResponse.<T>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Creates a PageResponse from Spring Data's Page object using direct content.
     *
     * @param page the Spring Data page
     * @param <T>  the content type
     * @return the page response
     */
    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .items(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Creates an empty page response.
     *
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param <T>        the content type
     * @return an empty page response
     */
    public static <T> PageResponse<T> empty(int pageNumber, int pageSize) {
        return PageResponse.<T>builder()
                .items(List.of())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalItems(0)
                .totalPages(0)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}

