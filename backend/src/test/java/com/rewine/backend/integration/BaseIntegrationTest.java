package com.rewine.backend.integration;

import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

/**
 * Base class for integration tests using Testcontainers with PostgreSQL.
 * Provides a shared PostgreSQL container for all integration tests.
 *
 * <p>Tests extending this class will be skipped if Docker is not available.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
@EnabledIf("isDockerAvailable")
public abstract class BaseIntegrationTest {

    /**
     * PostgreSQL version to use for tests.
     */
    private static final String POSTGRES_IMAGE = "postgres:15-alpine";

    /**
     * Flag indicating whether Docker is available.
     */
    private static final boolean DOCKER_AVAILABLE;

    /**
     * Shared PostgreSQL container for all integration tests.
     * Using static container to reuse across test classes for better performance.
     */
    protected static PostgreSQLContainer<?> postgresContainer;

    static {
        DOCKER_AVAILABLE = checkDockerAvailable();
        if (DOCKER_AVAILABLE) {
            try {
                postgresContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                        .withDatabaseName("rewine_test")
                        .withUsername("test")
                        .withPassword("test");
                postgresContainer.start();
            } catch (Exception ex) {
                // Container failed to start - tests will be skipped
                postgresContainer = null;
            }
        }
    }

    /**
     * Check Docker availability once at class load time.
     *
     * @return true if Docker daemon is accessible
     */
    private static boolean checkDockerAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", "info");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Checks if Docker is available for running tests.
     * Used by @EnabledIf annotation to conditionally enable tests.
     *
     * @return true if Docker daemon is accessible and container is running
     */
    static boolean isDockerAvailable() {
        return DOCKER_AVAILABLE
                && Objects.nonNull(postgresContainer)
                && postgresContainer.isRunning();
    }

    /**
     * Registers dynamic properties for Spring to use the Testcontainer datasource.
     *
     * @param registry the dynamic property registry
     */
    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        if (Objects.nonNull(postgresContainer) && postgresContainer.isRunning()) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
            registry.add("spring.datasource.username", postgresContainer::getUsername);
            registry.add("spring.datasource.password", postgresContainer::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

            // Disable Flyway to avoid circular dependency with entityManagerFactory
            registry.add("spring.flyway.enabled", () -> false);

            // JPA settings for Postgres - use create-drop for tests
            registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
            registry.add("spring.jpa.show-sql", () -> false);

            // Disable SQL init scripts
            registry.add("spring.sql.init.mode", () -> "never");
        }
    }
}
