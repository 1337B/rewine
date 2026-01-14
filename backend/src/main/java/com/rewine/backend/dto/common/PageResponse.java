package com.rewine.backend.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper.
 *
 * @param <T> the type of content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * The content items for the current page.
     */
    private List<T> content;

    /**
     * Current page number (0-indexed).
     */
    private int page;

    /**
     * Number of items per page.
     */
    private int size;

    /**
     * Total number of elements across all pages.
     */
    private long totalElements;

    /**
     * Total number of pages.
     */
    private int totalPages;

    /**
     * Whether this is the first page.
     */
    private boolean first;

    /**
     * Whether this is the last page.
     */
    private boolean last;

    /**
     * Whether there are more pages after this one.
     */
    private boolean hasNext;

    /**
     * Whether there are pages before this one.
     */
    private boolean hasPrevious;

    /**
     * Creates a PageResponse from Spring Data's Page object.
     *
     * @param page    the Spring Data page
     * @param content the mapped content
     * @param <T>     the content type
     * @param <E>     the entity type
     * @return the page response
     */
    public static <T, E> PageResponse<T> of(org.springframework.data.domain.Page<E> page, List<T> content) {
        return PageResponse.<T>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}

