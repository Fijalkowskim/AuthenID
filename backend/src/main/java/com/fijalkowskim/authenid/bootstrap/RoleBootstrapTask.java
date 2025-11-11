package com.fijalkowskim.authenid.bootstrap;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "authenid.bootstrap.roles.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RoleBootstrapTask implements BootstrapTask {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run() {
        Permission userRead = getPermission("USER_READ");
        Permission userManage = getPermission("USER_MANAGE");
        Permission clientRead = getPermission("CLIENT_READ");
        Permission clientManage = getPermission("CLIENT_MANAGE");

        createRoleIfMissing("USER", Set.of(userRead));
        createRoleIfMissing("SYSTEM_ADMIN", Set.of(userRead, userManage, clientRead, clientManage));
    }

    private Permission getPermission(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Permission not found: " + name));
    }

    private void createRoleIfMissing(String name, Set<Permission> permissions) {
        Optional<Role> existingOpt = roleRepository.findByName(name);
        if (existingOpt.isPresent()) {
            return;
        }

        Role role = new Role();
        role.setName(name);
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);
        log.info("Bootstrap role created: {}", name);
    }
}
