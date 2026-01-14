package com.rewine.backend.repository;

import com.rewine.backend.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for Role entity.
 */
@Repository
public interface IRoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * Finds a role by name.
     *
     * @param name the role name to search for
     * @return optional containing role if found
     */
    Optional<RoleEntity> findByName(String name);

    /**
     * Checks if a role exists by name.
     *
     * @param name the role name to check
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Finds roles by names.
     *
     * @param names the set of role names to search for
     * @return set of matching roles
     */
    Set<RoleEntity> findByNameIn(Set<String> names);

    /**
     * Finds all roles ordered by name.
     *
     * @return list of roles ordered by name
     */
    List<RoleEntity> findAllByOrderByNameAsc();

    /**
     * Counts users with a specific role.
     *
     * @param roleName the role name
     * @return count of users with the role
     */
    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersWithRole(@Param("roleName") String roleName);

    /**
     * Finds the default user role.
     *
     * @return optional containing the default user role
     */
    default Optional<RoleEntity> findDefaultUserRole() {
        return findByName(RoleEntity.ROLE_USER);
    }
}

