package com.fijalkowskim.authenid.service.role;

import com.fijalkowskim.authenid.model.role.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {

    Role create(Role role);

    Role update(Long id, Role role);

    void delete(Long id);

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

    List<Role> findAll();
}
