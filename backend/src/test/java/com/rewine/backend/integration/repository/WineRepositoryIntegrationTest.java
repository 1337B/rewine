package com.rewine.backend.integration.repository;

import com.rewine.backend.integration.BaseIntegrationTest;
import com.rewine.backend.model.entity.WineEntity;
import com.rewine.backend.model.entity.WineryEntity;
import com.rewine.backend.model.enums.WineType;
import com.rewine.backend.repository.IWineRepository;
import com.rewine.backend.repository.IWineryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for IWineRepository using Testcontainers with PostgreSQL.
 */
@Transactional
@DisplayName("Wine Repository Integration Tests")
class WineRepositoryIntegrationTest extends BaseIntegrationTest {

    /** Test vintage year for red wine. */
    private static final int VINTAGE_RED_WINE = 2020;

    /** Test vintage year for white wine. */
    private static final int VINTAGE_WHITE_WINE = 2021;

    /** Test vintage year for featured wine. */
    private static final int VINTAGE_FEATURED_WINE = 2019;

    /** Expected total number of test wines. */
    private static final int TOTAL_TEST_WINES = 3;

    /** Expected number of wines matching "Reserva" search. */
    private static final int EXPECTED_RESERVA_MATCHES = 2;

    /** Expected total pages when page size is 2. */
    private static final int EXPECTED_TOTAL_PAGES = 2;

    @Autowired
    private IWineRepository wineRepository;

    @Autowired
    private IWineryRepository wineryRepository;

    private WineryEntity testWinery;
    private WineEntity redWine;
    private WineEntity whiteWine;
    private WineEntity featuredWine;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        wineRepository.deleteAll();
        wineryRepository.deleteAll();

        // Create test winery
        testWinery = WineryEntity.builder()
                .name("Bodega Test")
                .country("Argentina")
                .region("Mendoza")
                .subregion("Luján de Cuyo")
                .description("A test winery for integration tests")
                .build();
        testWinery = wineryRepository.save(testWinery);

        // Create test wines
        redWine = WineEntity.builder()
                .name("Malbec Reserva")
                .winery(testWinery)
                .wineType(WineType.RED)
                .vintage(VINTAGE_RED_WINE)
                .style("Full-bodied")
                .grapes(List.of("Malbec"))
                .alcoholContent(new BigDecimal("14.5"))
                .priceMin(new BigDecimal("25.00"))
                .priceMax(new BigDecimal("35.00"))
                .ratingAverage(new BigDecimal("4.5"))
                .ratingCount(100)
                .isActive(true)
                .isFeatured(false)
                .descriptionEn("A full-bodied Malbec with notes of dark fruit")
                .build();

        whiteWine = WineEntity.builder()
                .name("Chardonnay Gran Reserva")
                .winery(testWinery)
                .wineType(WineType.WHITE)
                .vintage(VINTAGE_WHITE_WINE)
                .style("Medium-bodied")
                .grapes(List.of("Chardonnay"))
                .alcoholContent(new BigDecimal("13.0"))
                .priceMin(new BigDecimal("30.00"))
                .priceMax(new BigDecimal("45.00"))
                .ratingAverage(new BigDecimal("4.2"))
                .ratingCount(50)
                .isActive(true)
                .isFeatured(false)
                .descriptionEn("An elegant Chardonnay with citrus notes")
                .build();

        featuredWine = WineEntity.builder()
                .name("Premium Blend")
                .winery(testWinery)
                .wineType(WineType.RED)
                .vintage(VINTAGE_FEATURED_WINE)
                .style("Full-bodied")
                .grapes(List.of("Malbec", "Cabernet Sauvignon"))
                .alcoholContent(new BigDecimal("15.0"))
                .priceMin(new BigDecimal("80.00"))
                .priceMax(new BigDecimal("120.00"))
                .ratingAverage(new BigDecimal("4.8"))
                .ratingCount(200)
                .isActive(true)
                .isFeatured(true)
                .descriptionEn("A premium blend showcasing the best of Mendoza")
                .build();

        redWine = wineRepository.save(redWine);
        whiteWine = wineRepository.save(whiteWine);
        featuredWine = wineRepository.save(featuredWine);
    }

    @Nested
    @DisplayName("Find by ID with Winery")
    class FindByIdWithWineryTests {

        @Test
        @DisplayName("should find wine with winery eagerly loaded")
        void shouldFindWineWithWinery() {
            Optional<WineEntity> result = wineRepository.findByIdWithWinery(redWine.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Malbec Reserva");
            assertThat(result.get().getWinery()).isNotNull();
            assertThat(result.get().getWinery().getName()).isEqualTo("Bodega Test");
        }

        @Test
        @DisplayName("should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() {
            Optional<WineEntity> result = wineRepository.findByIdWithWinery(UUID.randomUUID());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search by Name")
    class SearchByNameTests {

        @Test
        @DisplayName("should find wines by partial name match")
        void shouldFindWinesByPartialName() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.searchByName("Malbec", pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Malbec Reserva");
        }

        @Test
        @DisplayName("should find wines by case-insensitive name")
        void shouldFindWinesByCaseInsensitiveName() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.searchByName("malbec", pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty page for no matches")
        void shouldReturnEmptyForNoMatches() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.searchByName("Pinot Noir", pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search Wines (Multi-criteria)")
    class SearchWinesTests {

        @Test
        @DisplayName("should search wines by winery name")
        void shouldSearchByWineryName() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.searchWines("Bodega Test", pageable);

            assertThat(result.getContent()).hasSize(TOTAL_TEST_WINES);
        }

        @Test
        @DisplayName("should search wines by description")
        void shouldSearchByDescription() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.searchWines("dark fruit", pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Malbec Reserva");
        }

        @Test
        @DisplayName("should search wines by wine name")
        void shouldSearchByWineName() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.searchWines("Reserva", pageable);

            assertThat(result.getContent()).hasSize(2); // Malbec Reserva and Chardonnay Gran Reserva
        }
    }

    @Nested
    @DisplayName("Find by Wine Type")
    class FindByWineTypeTests {

        @Test
        @DisplayName("should find active red wines")
        void shouldFindActiveRedWines() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.findByWineTypeAndIsActiveTrue(WineType.RED, pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent())
                    .extracting(WineEntity::getWineType)
                    .containsOnly(WineType.RED);
        }

        @Test
        @DisplayName("should find active white wines")
        void shouldFindActiveWhiteWines() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.findByWineTypeAndIsActiveTrue(WineType.WHITE, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Chardonnay Gran Reserva");
        }
    }

    @Nested
    @DisplayName("Find Featured Wines")
    class FindFeaturedWinesTests {

        @Test
        @DisplayName("should find only featured active wines")
        void shouldFindOnlyFeaturedActiveWines() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Premium Blend");
            assertThat(result.getContent().get(0).getIsFeatured()).isTrue();
        }

        @Test
        @DisplayName("should not return inactive featured wines")
        void shouldNotReturnInactiveFeaturedWines() {
            // Make featured wine inactive
            featuredWine.setIsActive(false);
            wineRepository.save(featuredWine);

            Pageable pageable = PageRequest.of(0, 10);
            Page<WineEntity> result = wineRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find by Winery")
    class FindByWineryTests {

        @Test
        @DisplayName("should find wines by winery ID")
        void shouldFindWinesByWineryId() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.findByWineryIdAndIsActiveTrue(testWinery.getId(), pageable);

            assertThat(result.getContent()).hasSize(TOTAL_TEST_WINES);
        }

        @Test
        @DisplayName("should return empty for unknown winery")
        void shouldReturnEmptyForUnknownWinery() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.findByWineryIdAndIsActiveTrue(UUID.randomUUID(), pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Find Top Rated")
    class FindTopRatedTests {

        @Test
        @DisplayName("should return wines ordered by rating")
        void shouldReturnWinesOrderedByRating() {
            Pageable pageable = PageRequest.of(0, 10);

            Page<WineEntity> result = wineRepository.findTopRated(pageable);

            assertThat(result.getContent()).hasSize(TOTAL_TEST_WINES);
            // Premium Blend has highest rating (4.8)
            assertThat(result.getContent().get(0).getName()).isEqualTo("Premium Blend");
        }
    }

    @Nested
    @DisplayName("Pagination")
    class PaginationTests {

        @Test
        @DisplayName("should respect page size")
        void shouldRespectPageSize() {
            Pageable pageable = PageRequest.of(0, 2);

            Page<WineEntity> result = wineRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(TOTAL_TEST_WINES);
            assertThat(result.getTotalPages()).isEqualTo(EXPECTED_TOTAL_PAGES);
        }

        @Test
        @DisplayName("should return correct page")
        void shouldReturnCorrectPage() {
            Pageable pageable = PageRequest.of(1, 2);

            Page<WineEntity> result = wineRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getNumber()).isEqualTo(1);
        }
    }
}

