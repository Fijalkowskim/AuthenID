package com.fijalkowskim.authenid.bootstrap;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Bootstrap task that creates default permissions (USER_READ, USER_MANAGE, CLIENT_READ, CLIENT_MANAGE)
 * if they do not already exist. Runs at order 10, before roles and users.
 */
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
        createIfMissing("ORDERS_READ", "View orders");
        createIfMissing("ORDERS_MANAGE", "Create and manage orders");
        createIfMissing("PRICING_READ", "View pricing and discounts");
        createIfMissing("PRICING_MANAGE", "Manage pricing and discounts");
        createIfMissing("ACCOUNT_MANAGE", "Manage company account");
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
