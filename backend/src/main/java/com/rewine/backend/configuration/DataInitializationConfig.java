package com.rewine.backend.configuration;

import com.rewine.backend.model.entity.RoleEntity;
import com.rewine.backend.repository.IRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * Configuration to initialize default data on application startup.
 */
@Configuration
public class DataInitializationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializationConfig.class);

    /**
     * Initializes default roles if they don't exist.
     */
    @Bean
    @Order(1)
    public CommandLineRunner initializeRoles(IRoleRepository roleRepository) {
        return args -> {
            List<String> defaultRoles = List.of(
                    RoleEntity.ROLE_USER,
                    RoleEntity.ROLE_ADMIN,
                    RoleEntity.ROLE_MODERATOR,
                    RoleEntity.ROLE_PARTNER
            );

            for (String roleName : defaultRoles) {
                if (!roleRepository.existsByName(roleName)) {
                    RoleEntity role = RoleEntity.builder()
                            .name(roleName)
                            .description(getDefaultDescription(roleName))
                            .build();
                    roleRepository.save(role);
                    LOGGER.info("Created default role: {}", roleName);
                }
            }

            LOGGER.info("Role initialization completed. Total roles: {}", roleRepository.count());
        };
    }

    /**
     * Gets default description for a role.
     */
    private String getDefaultDescription(String roleName) {
        return switch (roleName) {
            case RoleEntity.ROLE_USER -> "Standard user role";
            case RoleEntity.ROLE_ADMIN -> "Administrator with full access";
            case RoleEntity.ROLE_MODERATOR -> "Content moderator role";
            case RoleEntity.ROLE_PARTNER -> "Business partner role";
            default -> "Role: " + roleName;
        };
    }
}

