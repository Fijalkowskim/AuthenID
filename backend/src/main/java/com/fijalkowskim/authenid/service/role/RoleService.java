package com.fijalkowskim.authenid.service.role;

import com.fijalkowskim.authenid.model.role.Role;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for role management operations.
 * Provides CRUD operations and lookup methods for {@link Role} entities.
 */
public interface RoleService {

    Role create(Role role);

    Role update(Long id, Role role);

    void delete(Long id);

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

    List<Role> findAll();
}
