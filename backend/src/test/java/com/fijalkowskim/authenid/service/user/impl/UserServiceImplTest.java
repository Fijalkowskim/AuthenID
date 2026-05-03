package com.fijalkowskim.authenid.service.user.impl;

import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.model.user.UserStatus;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import com.fijalkowskim.authenid.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link UserServiceImpl}.
 * Uses H2 in-memory database (test profile) and rolls back after each test.
 */
@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserServiceImpl userService;

    @Test
    void create_shouldPersistUserAndReturnIt() {
        User user = buildUser("alice", "alice@example.com");

        User saved = userService.create(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("alice");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void create_shouldThrowWhenEmailAlreadyExists() {
        userRepository.save(buildUser("existing", "dup@example.com"));

        User duplicate = buildUser("new", "dup@example.com");
        assertThatThrownBy(() -> userService.create(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void create_shouldThrowWhenUsernameAlreadyExists() {
        userRepository.save(buildUser("dupname", "first@example.com"));

        User duplicate = buildUser("dupname", "second@example.com");
        assertThatThrownBy(() -> userService.create(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already in use");
    }

    @Test
    void update_shouldModifyMutableFields() {
        User saved = userRepository.save(buildUser("bob", "bob@example.com"));

        User patch = User.builder()
                .username("bob_updated")
                .passwordHash("hash")
                .email("bob_new@example.com")
                .status(UserStatus.ACTIVE)
                .build();

        User updated = userService.update(saved.getId(), patch);

        assertThat(updated.getUsername()).isEqualTo("bob_updated");
        assertThat(updated.getEmail()).isEqualTo("bob_new@example.com");
    }

    @Test
    void update_shouldThrowWhenEmailConflictsWithOtherUser() {
        userRepository.save(buildUser("charlie", "charlie@example.com"));
        User bob = userRepository.save(buildUser("bob", "bob@example.com"));

        User patch = User.builder()
                .username("bob")
                .passwordHash("hash")
                .email("charlie@example.com")
                .status(UserStatus.ACTIVE)
                .build();

        assertThatThrownBy(() -> userService.update(bob.getId(), patch))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        User patch = buildUser("x", "x@example.com");
        assertThatThrownBy(() -> userService.update(999L, patch))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveUser() {
        User saved = userRepository.save(buildUser("dave", "dave@example.com"));

        userService.delete(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void delete_shouldThrowWhenUserNotFound() {
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userRepository.save(buildUser("user1", "u1@example.com"));
        userRepository.save(buildUser("user2", "u2@example.com"));

        List<User> all = userService.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        User saved = userRepository.save(buildUser("eve", "eve@example.com"));

        Optional<User> found = userService.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("eve");
    }

    @Test
    void findById_shouldReturnEmptyWhenMissing() {
        assertThat(userService.findById(99999L)).isEmpty();
    }

    private User buildUser(String username, String email) {
        return User.builder()
                .username(username)
                .passwordHash("encoded_pass")
                .email(email)
                .status(UserStatus.ACTIVE)
                .build();
    }
}
