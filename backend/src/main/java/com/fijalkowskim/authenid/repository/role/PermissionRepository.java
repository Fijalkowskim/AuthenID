package com.fijalkowskim.authenid.repository.role;

import com.fijalkowskim.authenid.model.role.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    List<Permission> findAllByNameIn(Collection<String> names);
}
