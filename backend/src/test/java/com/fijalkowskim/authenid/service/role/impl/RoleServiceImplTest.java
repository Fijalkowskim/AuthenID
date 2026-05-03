package com.fijalkowskim.authenid.service.role.impl;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link RoleServiceImpl}.
 * Uses H2 in-memory database (test profile) and rolls back after each test.
 */
@SpringBootTest
@Transactional
class RoleServiceImplTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Test
    void create_shouldPersistRoleAndReturnIt() {
        Role role = Role.builder().name("TEST_ROLE").description("A test role").build();

        Role saved = roleService.create(role);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("TEST_ROLE");
    }

    @Test
    void create_shouldThrowWhenRoleAlreadyExists() {
        roleRepository.save(Role.builder().name("DUPLICATE").build());

        Role dup = Role.builder().name("DUPLICATE").build();
        assertThatThrownBy(() -> roleService.create(dup))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role already exists");
    }

    @Test
    void update_shouldModifyNameAndDescription() {
        Role saved = roleRepository.save(Role.builder().name("OLD_NAME").build());

        Role patch = Role.builder().name("NEW_NAME").description("Updated").build();
        Role updated = roleService.update(saved.getId(), patch);

        assertThat(updated.getName()).isEqualTo("NEW_NAME");
        assertThat(updated.getDescription()).isEqualTo("Updated");
    }

    @Test
    void update_shouldThrowWhenRoleNotFound() {
        Role patch = Role.builder().name("X").build();
        assertThatThrownBy(() -> roleService.update(999L, patch))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveRole() {
        Role saved = roleRepository.save(Role.builder().name("TO_DELETE").build());

        roleService.delete(saved.getId());

        assertThat(roleRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void delete_shouldThrowWhenRoleNotFound() {
        assertThatThrownBy(() -> roleService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllRoles() {
        roleRepository.save(Role.builder().name("ROLE_A").build());
        roleRepository.save(Role.builder().name("ROLE_B").build());

        List<Role> all = roleService.findAll();

        assertThat(all.stream().map(Role::getName))
                .contains("ROLE_A", "ROLE_B");
    }

    @Test
    void findByName_shouldReturnRoleWhenExists() {
        roleRepository.save(Role.builder().name("FINDER_ROLE").build());

        assertThat(roleService.findByName("FINDER_ROLE")).isPresent();
    }

    @Test
    void findByName_shouldReturnEmptyWhenMissing() {
        assertThat(roleService.findByName("NONEXISTENT")).isEmpty();
    }
}
