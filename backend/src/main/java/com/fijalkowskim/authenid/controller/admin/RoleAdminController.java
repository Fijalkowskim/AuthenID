package com.fijalkowskim.authenid.controller.admin;

import com.fijalkowskim.authenid.dto.role.RoleCreateRequest;
import com.fijalkowskim.authenid.dto.role.RoleResponse;
import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import com.fijalkowskim.authenid.service.role.RoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative REST API for managing roles.
 * All endpoints require the SYSTEM_ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class RoleAdminController {

    private final RoleService roleService;
    private final PermissionRepository permissionRepository;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param roleService           service for role operations
     * @param permissionRepository  repository for resolving permissions by name
     */
    public RoleAdminController(RoleService roleService, PermissionRepository permissionRepository) {
        this.roleService = roleService;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Returns a list of all roles.
     *
     * @return list of role response DTOs
     */
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> roles = roleService.findAll().stream()
                .map(RoleResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    /**
     * Returns a single role by ID.
     *
     * @param id role identifier
     * @return role response DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable Long id) {
        return roleService.findById(id)
                .map(RoleResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new role with optional permissions.
     *
     * @param request role creation parameters
     * @return created role response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
        Set<Permission> permissions = resolvePermissions(request.permissionNames());

        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .permissions(permissions)
                .build();

        Role saved = roleService.create(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleResponse.from(saved));
    }

    /**
     * Deletes a role by ID.
     *
     * @param id role identifier
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Set<Permission> resolvePermissions(Set<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + name)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
