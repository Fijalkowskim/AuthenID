package com.fijalkowskim.authenid.bootstrap;

import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.model.user.UserStatus;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import com.fijalkowskim.authenid.repository.user.UserRepository;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bootstrap task that creates the default admin user if one does not already exist.
 * The admin password is resolved in order: configured value → random → default "nimda".
 * Runs at order 30, after roles are created.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "authenid.bootstrap.users.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Order(30)
public class UserBootstrapTask implements BootstrapTask {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${authenid.bootstrap.users.random-password:false}")
    private boolean randomPasswordEnabled;

    @Value("${authenid.bootstrap.users.admin-password:}")
    private String configuredAdminPassword;

    @Override
    public void run() {
        Optional<User> existingAdmin = userRepository.findWithRolesByUsername("admin");
        if (existingAdmin.isPresent()) {
            return;
        }

        String rawPassword = resolveAdminPassword();

        Role adminRole = roleRepository.findByName("SYSTEM_ADMIN")
                .orElseThrow(() -> new IllegalStateException("Admin role SYSTEM_ADMIN not found"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@authenid.local");
        admin.setPasswordHash(passwordEncoder.encode(rawPassword));
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRoles(Set.of(adminRole));

        userRepository.save(admin);
        log.info("Bootstrap admin user created. Username: admin, password: {}", rawPassword);

        createB2bUsersIfMissing();
    }

    private void createB2bUsersIfMissing() {
        createUserIfMissing("buyer1",   "buyer1@techparts.demo",   "Buyer1234!",  "B2B_BUYER");
        createUserIfMissing("buyer2",   "buyer2@globaltech.demo",  "Buyer1234!",  "B2B_BUYER");
        createUserIfMissing("sales1",   "sales1@techparts.demo",   "Sales1234!",  "B2B_SALES");
        createUserIfMissing("b2badmin", "admin@techparts.demo",    "Admin1234!",  "B2B_ADMIN");
    }

    private void createUserIfMissing(String username, String email, String password, String roleName) {
        if (userRepository.findWithRolesByUsername(username).isPresent()) {
            return;
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(role));
        userRepository.save(user);
        log.info("Bootstrap user created: {} ({})", username, roleName);
    }

    private String resolveAdminPassword() {
        if (randomPasswordEnabled) {
            return generateRandomPassword(16);
        }
        if (configuredAdminPassword != null && !configuredAdminPassword.isBlank()) {
            return configuredAdminPassword;
        }
        return "nimda";
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            builder.append(chars.charAt(index));
        }
        return builder.toString();
    }
}
