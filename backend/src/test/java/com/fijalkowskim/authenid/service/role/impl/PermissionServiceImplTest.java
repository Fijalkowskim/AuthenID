package com.fijalkowskim.authenid.service.role.impl;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link PermissionServiceImpl}.
 * Uses H2 in-memory database (test profile) and rolls back after each test.
 */
@SpringBootTest
@Transactional
class PermissionServiceImplTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionServiceImpl permissionService;

    @Test
    void create_shouldPersistPermissionAndReturnIt() {
        Permission perm = Permission.builder().name("TEST_PERM").description("desc").build();

        Permission saved = permissionService.create(perm);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("TEST_PERM");
    }

    @Test
    void create_shouldThrowWhenPermissionAlreadyExists() {
        permissionRepository.save(Permission.builder().name("EXISTING_PERM").build());

        Permission dup = Permission.builder().name("EXISTING_PERM").build();
        assertThatThrownBy(() -> permissionService.create(dup))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Permission already exists");
    }

    @Test
    void update_shouldModifyNameAndDescription() {
        Permission saved = permissionRepository.save(Permission.builder().name("OLD_PERM").build());

        Permission patch = Permission.builder().name("NEW_PERM").description("Updated desc").build();
        Permission updated = permissionService.update(saved.getId(), patch);

        assertThat(updated.getName()).isEqualTo("NEW_PERM");
        assertThat(updated.getDescription()).isEqualTo("Updated desc");
    }

    @Test
    void update_shouldThrowWhenPermissionNotFound() {
        Permission patch = Permission.builder().name("X").build();
        assertThatThrownBy(() -> permissionService.update(999L, patch))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_shouldRemovePermission() {
        Permission saved = permissionRepository.save(Permission.builder().name("TO_DEL").build());

        permissionService.delete(saved.getId());

        assertThat(permissionRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void delete_shouldThrowWhenPermissionNotFound() {
        assertThatThrownBy(() -> permissionService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllPermissions() {
        permissionRepository.save(Permission.builder().name("PERM_X").build());
        permissionRepository.save(Permission.builder().name("PERM_Y").build());

        List<Permission> all = permissionService.findAll();

        assertThat(all.stream().map(Permission::getName))
                .contains("PERM_X", "PERM_Y");
    }

    @Test
    void findByName_shouldReturnPermissionWhenExists() {
        permissionRepository.save(Permission.builder().name("FIND_ME").build());

        assertThat(permissionService.findByName("FIND_ME")).isPresent();
    }

    @Test
    void findByName_shouldReturnEmptyWhenMissing() {
        assertThat(permissionService.findByName("GHOST")).isEmpty();
    }
}
