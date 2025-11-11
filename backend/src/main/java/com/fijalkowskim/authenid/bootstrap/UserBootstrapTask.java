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
