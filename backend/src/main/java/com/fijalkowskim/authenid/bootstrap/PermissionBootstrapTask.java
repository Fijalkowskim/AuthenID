package com.fijalkowskim.authenid.bootstrap;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "authenid.bootstrap.permissions.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class PermissionBootstrapTask implements BootstrapTask {

    private final PermissionRepository permissionRepository;

    @Override
    public void run() {
        createIfMissing("USER_READ", "Read users");
        createIfMissing("USER_MANAGE", "Create and update users");
        createIfMissing("CLIENT_READ", "Read OIDC clients");
        createIfMissing("CLIENT_MANAGE", "Create and update OIDC clients");
    }

    private Permission createIfMissing(String name, String description) {
        Optional<Permission> existing = permissionRepository.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        Permission saved = permissionRepository.save(permission);
        log.info("Bootstrap permission created: {}", name);
        return saved;
    }
}
