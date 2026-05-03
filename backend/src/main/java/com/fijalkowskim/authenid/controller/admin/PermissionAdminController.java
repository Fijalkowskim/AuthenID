package com.fijalkowskim.authenid.controller.admin;

import com.fijalkowskim.authenid.dto.role.PermissionCreateRequest;
import com.fijalkowskim.authenid.dto.role.PermissionResponse;
import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.service.role.PermissionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative REST API for managing permissions.
 * All endpoints require the SYSTEM_ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/permissions")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class PermissionAdminController {

    private final PermissionService permissionService;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param permissionService service for permission operations
     */
    public PermissionAdminController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Returns a list of all permissions.
     *
     * @return list of permission response DTOs
     */
    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        List<PermissionResponse> permissions = permissionService.findAll().stream()
                .map(PermissionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }

    /**
     * Returns a single permission by ID.
     *
     * @param id permission identifier
     * @return permission response DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable Long id) {
        return permissionService.findById(id)
                .map(PermissionResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new permission.
     *
     * @param request permission creation parameters
     * @return created permission response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(
            @Valid @RequestBody PermissionCreateRequest request) {
        Permission permission = Permission.builder()
                .name(request.name())
                .description(request.description())
                .build();
        Permission saved = permissionService.create(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(PermissionResponse.from(saved));
    }

    /**
     * Deletes a permission by ID.
     *
     * @param id permission identifier
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
