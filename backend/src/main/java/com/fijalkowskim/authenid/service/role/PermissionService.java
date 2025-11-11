package com.fijalkowskim.authenid.service.role;

import com.fijalkowskim.authenid.model.role.Permission;
import java.util.List;
import java.util.Optional;

public interface PermissionService {

    Permission create(Permission permission);

    Permission update(Long id, Permission permission);

    void delete(Long id);

    Optional<Permission> findById(Long id);

    Optional<Permission> findByName(String name);

    List<Permission> findAll();
}
